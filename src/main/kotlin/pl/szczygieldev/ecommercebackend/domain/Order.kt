package pl.szczygieldev.ecommercebackend.domain

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.domain.error.*
import pl.szczygieldev.ecommercebackend.domain.event.*
import pl.szczygieldev.shared.ddd.core.EventSourcedEntity
import java.math.BigDecimal
import java.time.Instant

class Order private constructor(
    val orderId: OrderId
) : EventSourcedEntity<OrderEvent>() {
    private var status: OrderStatus = OrderStatus.CREATED
    private lateinit var cartId: CartId
    private lateinit var payment: Payment
    private lateinit var delivery: Delivery
    private lateinit var createdAt: Instant

    companion object {
        fun create(
            orderId: OrderId,
            cartId: CartId,
            amount: BigDecimal,
            paymentDetails: PaymentDetails,
            deliveryProvider: DeliveryProvider,
        ): Order {
            val order = Order(orderId)
            order.raiseEvent(OrderCreated(orderId, cartId, amount, paymentDetails, deliveryProvider))
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
        if (status != OrderStatus.CREATED) {
            raise(AlreadyAcceptedOrderError.forId(orderId))
        }
        raiseEvent(OrderAccepted(orderId))
    }

    fun reject(): Either<OrderError, Unit> = either {
        if (status != OrderStatus.CREATED) {
            raise(AlreadyAcceptedOrderError.forId(orderId))
        }
        raiseEvent(OrderRejected(orderId))
    }


    fun cancel(): Either<OrderError, Unit> = either {
        if (status == OrderStatus.SENT) {
            raise(CannotCancelSentOrderError.forId(orderId))
        }
        raiseEvent(OrderCanceled(orderId))
        //TODO after cancel refund process should start
    }

    fun returnOrder(): Either<OrderError, Unit> = either {
        if (delivery.status != DeliveryStatus.DELIVERED) {
            raise(CannotReturnNotReceivedOrderError.forId(orderId))
        }
        //TODO
    }

    fun beginPacking(): Either<OrderError, Unit> = either {
        if (status != OrderStatus.ACCEPTED) {
            raise(CannotPackageNotAcceptedOrderError.forId(orderId))
        }
        if (!payment.isPaid) {
            raise(NotPaidOrderError.forId(orderId))
        }
        raiseEvent(OrderPackagingStarted(orderId))
    }

    fun completePacking(
        parcelId: ParcelId,
        parcelDimensions: ParcelDimensions
    ): Either<OrderError, Unit> = either {
        if (status != OrderStatus.IN_PROGRESS) {
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
        payment =
            Payment(paymentDetails.id, paymentDetails.amount, paymentDetails.url, paymentDetails.paymentServiceProvider)
        delivery = Delivery(event.deliveryProvider, DeliveryStatus.WAITING, null)
        createdAt = event.occurredOn
    }


    private fun apply(event: OrderAccepted) {
        status = OrderStatus.ACCEPTED
    }

    private fun apply(event: OrderCanceled) {
        status = OrderStatus.CANCELLED
    }

    private fun apply(event: OrderDeliveryStatusChanged) {
        if(event.status != DeliveryStatus.WAITING){
            status = OrderStatus.SENT
        }
        delivery = delivery.copy(status = event.status)
    }

    private fun apply(event: OrderPackaged) {
        status = OrderStatus.READY

        delivery =
            delivery.copy(
                parcel = Parcel(event.parcelId, event.parcelDimensions)
            )
    }

    private fun apply(event: OrderPackagingStarted) {
        status = OrderStatus.IN_PROGRESS
    }

    private fun apply(event: OrderPaymentReceived) {
        payment.registerTransaction(event.paymentTransaction)
        if (payment.isPaid) {
            raiseEvent(OrderPaid(orderId))
        } else {
            raiseEvent(OrderInvalidAmountPaid(orderId, payment.sumOfTransactions, payment.amount))
        }
    }

    private fun apply(event: OrderRejected) {
        status = OrderStatus.REJECTED
    }


}