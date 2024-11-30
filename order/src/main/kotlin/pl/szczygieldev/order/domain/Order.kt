package pl.szczygieldev.order.domain

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercelibrary.ddd.core.EventSourcedEntity
import pl.szczygieldev.order.domain.error.*
import pl.szczygieldev.order.domain.event.*
import java.time.Instant

class Order private constructor(
    val orderId: OrderId
) : EventSourcedEntity<OrderEvent>() {
    private var _status: OrderStatus = OrderStatus.CREATED
    val status: OrderStatus
        get() = _status

    private lateinit var _cartId: CartId
    val cartId: CartId
        get() = _cartId

    private lateinit var _payment: Payment
    val payment: Payment
        get() = _payment.copy()

    private lateinit var _delivery: Delivery
    val delivery: Delivery
        get() = _delivery.copy()
    private lateinit var _createdAt: Instant

    private lateinit var _items: List<OrderItem>
    val items: List<OrderItem>
        get() = _items.map { item -> item.copy() }.toList()

    data class OrderItem(val productId: ProductId, val quantity: Int)

    companion object {
        fun create(
            orderId: OrderId,
            cartId: CartId,
            paymentDetails: PaymentDetails,
            deliveryProvider: DeliveryProvider, items: List<OrderItem>
        ): Order {
            val order = Order(orderId)
            order.raiseEvent(
                OrderCreated(
                    orderId,
                    cartId,
                    paymentDetails.amount,
                    paymentDetails,
                    deliveryProvider,
                    items
                )
            )
            return order
        }

        fun fromEvents(orderId: OrderId, events: List<OrderEvent>): Order {
            val order = Order(orderId)
            order.applyAll(events)
            order.clearOccurredEvents()
            return order
        }

    }

    fun accept(): Either<OrderError, Unit> = either {
        if (_status != OrderStatus.CREATED) {
            raise(AlreadyAcceptedOrderError.forId(orderId))
        }
        raiseEvent(OrderAccepted(orderId))
    }

    fun reject(): Either<OrderError, Unit> = either {
        if (_status != OrderStatus.CREATED) {
            raise(AlreadyAcceptedOrderError.forId(orderId))
        }
        raiseEvent(OrderRejected(orderId))
    }


    fun cancel(): Either<OrderError, Unit> = either {
        if (_status == OrderStatus.SENT) {
            raise(CannotCancelSentOrderError.forId(orderId))
        }
        raiseEvent(OrderCanceled(orderId))
        //TODO after cancel refund process should start
    }

    fun returnOrder(): Either<OrderError, Unit> = either {
        if (_delivery.status != DeliveryStatus.DELIVERED) {
            raise(CannotReturnNotReceivedOrderError.forId(orderId))
        }
        //TODO
    }

    fun beginPacking(): Either<OrderError, Unit> = either {
        if (_status != OrderStatus.ACCEPTED) {
            raise(CannotPackageNotAcceptedOrderError.forId(orderId))
        }
        if (!_payment.isPaid) {
            raise(NotPaidOrderError.forId(orderId))
        }
        raiseEvent(OrderPackagingStarted(orderId))
    }

    fun completePacking(
        parcelId: ParcelId,
        parcelDimensions: ParcelDimensions
    ): Either<OrderError, Unit> = either {
        if (_status != OrderStatus.IN_PROGRESS) {
            raise(PackingNotInProgressError.forId(orderId))
        }
        raiseEvent(OrderPackaged(orderId, parcelId, parcelDimensions))
    }

    fun changeDeliveryStatus(deliveryStatus: DeliveryStatus) {
        raiseEvent(OrderDeliveryStatusChanged(orderId, deliveryStatus))
    }

    /*
    *   We want to save all incoming payments. If an order payment is unpaid in full, we should ask the client for additional payment.
    *   If the amount is over the desired value, we should refund the client. Mechanism to be implemented in the future.
    */
    fun pay(paymentTransaction: PaymentTransaction) {
        raiseEvent(OrderPaymentReceived(orderId, paymentTransaction))
    }

    override fun applyEvent(event: OrderEvent) {
        when (event) {
            is OrderAccepted -> apply(event)
            is OrderCanceled -> apply(event)
            is OrderCreated -> apply(event)
            is OrderDeliveryStatusChanged -> apply(event)
            is OrderPackaged -> apply(event)
            is OrderPackagingStarted -> apply(event)
            is OrderPaymentReceived -> apply(event)
            is OrderRejected -> apply(event)
            is OrderPaid -> {}
            is OrderInvalidAmountPaid -> {}
        }
    }

    private fun apply(event: OrderCreated) {
        val paymentDetails = event.paymentDetails
        _cartId = event.cartId
        _payment =
            Payment.create(
                paymentDetails.id,
                paymentDetails.amount,
                paymentDetails.url,
                paymentDetails.paymentServiceProvider
            )
        _delivery = Delivery(event.deliveryProvider, DeliveryStatus.WAITING, null)
        _createdAt = event.occurredOn
        _items = event.items
    }


    private fun apply(event: OrderAccepted) {
        _status = OrderStatus.ACCEPTED
    }

    private fun apply(event: OrderCanceled) {
        _status = OrderStatus.CANCELLED
    }

    private fun apply(event: OrderDeliveryStatusChanged) {
        if (event.status != DeliveryStatus.WAITING) {
            _status = OrderStatus.SENT
        }
        _delivery = _delivery.copy(status = event.status)
    }

    private fun apply(event: OrderPackaged) {
        _status = OrderStatus.READY

        _delivery =
            _delivery.copy(
                parcel = Parcel(event.parcelId, event.parcelDimensions)
            )
    }

    private fun apply(event: OrderPackagingStarted) {
        _status = OrderStatus.IN_PROGRESS
    }

    private fun apply(event: OrderPaymentReceived) {
        _payment.registerTransaction(event.paymentTransaction)
        if (_payment.isPaid) {
            raiseEvent(OrderPaid(orderId))
        } else {
            raiseEvent(OrderInvalidAmountPaid(orderId, _payment.sumOfTransactions, _payment.amount))
        }
    }

    private fun apply(event: OrderRejected) {
        _status = OrderStatus.REJECTED
    }
}