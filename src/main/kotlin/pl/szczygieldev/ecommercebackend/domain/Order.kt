package pl.szczygieldev.ecommercebackend.domain

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.domain.error.*
import java.math.BigDecimal

class Order private constructor(
    private val orderId: OrderId,
    private val cartId: CartId,
    private var payment: Payment,
    private var delivery: Delivery
) {
    private var status: OrderStatus = OrderStatus.CREATED

    companion object {
        fun create(
            orderId: OrderId,
            cartId: CartId,
            amount: BigDecimal,
            paymentServiceProvider: PaymentServiceProvider,
            deliveryProvider: DeliveryProvider,
        ): Order =
            Order(
                orderId,
                cartId,
                Payment(amount, paymentServiceProvider),
                Delivery(deliveryProvider, null, DeliveryStatus.WAITING)
            )
    }

    fun acceptOrder(): Either<OrderError, Unit> = either {
        if (status != OrderStatus.CREATED) {
            raise(AlreadyAcceptedOrderError.forId(orderId))
        }
        status = OrderStatus.ACCEPTED
    }

    fun rejectOrder(): Either<OrderError, Unit> = either {
        if (status != OrderStatus.CREATED) {
            raise(AlreadyAcceptedOrderError.forId(orderId))
        }
        status = OrderStatus.REJECTED
    }


    fun cancelOrder(): Either<OrderError, Unit> = either {
        if (status == OrderStatus.SENT) {
            raise(CannotCancelSentOrderError.forId(orderId))
        }
        status = OrderStatus.CANCELLED
        //after cancel refund process should start
    }

    fun returnOrder(): Either<OrderError, Unit> = either {
        if (delivery.status != DeliveryStatus.DELIVERED) {
            raise(CannotReturnNotReceivedOrderError.forId(orderId))
        }
    }

    fun beginPacking(): Either<OrderError, Unit> = either {
        if (status != OrderStatus.ACCEPTED) {
            raise(CannotPackageNotAcceptedOrderError.forId(orderId))
        }
        if (!payment.isPaid) {
            raise(NotPaidOrderError.forId(orderId))
        }
        status = OrderStatus.IN_PROGRESS
    }

    fun completePacking(): Either<OrderError, Unit> = either {
        if (status != OrderStatus.ACCEPTED) {
            raise(CannotPackageNotAcceptedOrderError.forId(orderId))
        }
        status = OrderStatus.READY
    }

    fun send(externalParcelIdentifier: String) {
        delivery =
            delivery.copy(status = DeliveryStatus.IN_DELIVERY, externalParcelIdentifier = externalParcelIdentifier)
    }

    fun delivered() {
        delivery = delivery.copy(status = DeliveryStatus.DELIVERED)
    }

    /*
    *   We want to save all incoming payments. If an order payment is unpaid in full, we should ask the client for additional payment.
    *   If the amount is over the desired value, we should refund the client. Mechanism to be implemented in the future.
    */
    fun pay(paymentTransaction: PaymentTransaction): Either<OrderError, Unit> = either {
        payment.registerTransaction(paymentTransaction)
        if (!payment.isPaid) {
            raise(InvalidPaymentAmountError.forId(orderId, payment.sumOfTransactions, payment.amount))
        }
    }
}