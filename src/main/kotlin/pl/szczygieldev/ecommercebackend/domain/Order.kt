package pl.szczygieldev.ecommercebackend.domain

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.domain.error.*
import pl.szczygieldev.ecommercebackend.domain.event.*
import pl.szczygieldev.shared.ddd.core.EventSourcedEntity
import java.math.BigDecimal

class Order private constructor(
    val orderId: OrderId
) : EventSourcedEntity<OrderEvent>() {
    private var status: OrderStatus = OrderStatus.CREATED
    private lateinit var cartId: CartId
    private lateinit var payment: Payment
    private lateinit var delivery: Delivery

    companion object {
        fun create(
            orderId: OrderId,
            cartId: CartId,
            amount: BigDecimal,
            paymentServiceProvider: PaymentServiceProvider,
            deliveryProvider: DeliveryProvider,
        ): Order {
            val order = Order(orderId)
            order.raiseEvent(OrderCreated(orderId, cartId, amount, paymentServiceProvider, deliveryProvider))
            return order
        }

        fun fromEvents(orderId: OrderId, events: List<OrderEvent>): Order {
            val order = Order(orderId)
            order.applyAll(events)
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

    fun completePacking(): Either<OrderError, Unit> = either {
        if (status != OrderStatus.ACCEPTED) {
            raise(CannotPackageNotAcceptedOrderError.forId(orderId))
        }
        raiseEvent(OrderPackaged(orderId))
    }

    fun send(externalParcelIdentifier: String) {
        raiseEvent(OrderSent(orderId, externalParcelIdentifier))
    }

    fun delivered() {
        raiseEvent(OrderDelivered(orderId))
    }

    /*
    *   We want to save all incoming payments. If an order payment is unpaid in full, we should ask the client for additional payment.
    *   If the amount is over the desired value, we should refund the client. Mechanism to be implemented in the future.
    */
    fun pay(paymentTransaction: PaymentTransaction): Either<OrderError, Unit> = either {
        raiseEvent(OrderPaymentReceived(orderId, paymentTransaction))
        if (!payment.isPaid) {
            raise(InvalidPaymentAmountError.forId(orderId, payment.sumOfTransactions, payment.amount))
        }
    }

    override fun applyEvent(event: OrderEvent) {
        when (event) {
            is OrderAccepted -> apply(event)
            is OrderCanceled -> apply(event)
            is OrderCreated -> apply(event)
            is OrderDelivered -> apply(event)
            is OrderPackaged -> apply(event)
            is OrderPackagingStarted -> apply(event)
            is OrderPaymentReceived -> apply(event)
            is OrderRejected -> apply(event)
            is OrderSent -> apply(event)
        }
    }

    private fun apply(event: OrderCreated) {
        payment = Payment(event.amount, event.paymentServiceProvider)
        delivery = Delivery(event.deliveryProvider, null, DeliveryStatus.WAITING)
    }


    private fun apply(event: OrderAccepted) {
        status = OrderStatus.ACCEPTED
    }

    private fun apply(event: OrderCanceled) {
        status = OrderStatus.CANCELLED
    }

    private fun apply(event: OrderDelivered) {
        delivery = delivery.copy(status = DeliveryStatus.DELIVERED)
    }

    private fun apply(event: OrderPackaged) {
        status = OrderStatus.READY
    }

    private fun apply(event: OrderPackagingStarted) {
        status = OrderStatus.IN_PROGRESS
    }

    private fun apply(event: OrderPaymentReceived) {
        payment.registerTransaction(event.paymentTransaction)
    }

    private fun apply(event: OrderRejected) {
        status = OrderStatus.REJECTED
    }

    private fun apply(event: OrderSent) {
        delivery =
            delivery.copy(
                status = DeliveryStatus.IN_DELIVERY,
                externalParcelIdentifier = event.externalParcelIdentifier
            )
    }
}