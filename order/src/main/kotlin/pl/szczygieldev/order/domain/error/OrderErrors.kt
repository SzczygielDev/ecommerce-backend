package pl.szczygieldev.order.domain.error

import pl.szczygieldev.order.domain.*
import java.math.BigDecimal

internal sealed class OrderError(message: String) : AppError(message)

internal data class CannotCancelSentOrderError(override val message: String) : OrderError(message) {
    companion object {
        fun forId(id: OrderId): CannotCancelSentOrderError {
            return CannotCancelSentOrderError("Cannot cancel order with id='${id.id()}' because its already sent")
        }
    }
}

internal data class CannotReturnNotReceivedOrderError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): CannotReturnNotReceivedOrderError {
            return CannotReturnNotReceivedOrderError("Cannot return order with id='${id.id()}' because its not received")
        }
    }
}

internal data class AlreadyAcceptedOrderError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): AlreadyAcceptedOrderError {
            return AlreadyAcceptedOrderError("Cannot accept or reject order with id='${id.id()}' because it has been already accepted")
        }
    }
}

internal data class NotPaidOrderError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): NotPaidOrderError {
            return NotPaidOrderError("Cannot process order with id='${id.id()}' because its not paid")
        }
    }
}

internal data class CannotPackageNotAcceptedOrderError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): CannotPackageNotAcceptedOrderError {
            return CannotPackageNotAcceptedOrderError("Cannot package order with id='${id.id()}' because its not accepted")
        }
    }
}

internal data class InvalidPaymentAmountError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId, currentAmount: BigDecimal, targetAmount: BigDecimal): InvalidPaymentAmountError {
            return InvalidPaymentAmountError("Invalid payment amount for order with id='${id.id()}' current amount='${currentAmount}' desired amount='${targetAmount}'")
        }
    }
}

internal data class OrderNotFoundError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): OrderNotFoundError {
            return OrderNotFoundError("Cannot find order with id='${id.id()}'.")
        }

        fun forParcelId(parcelId: ParcelId): OrderNotFoundError {
            return OrderNotFoundError("Cannot find order with parcel id='${parcelId.id}'.")
        }

        fun forPaymentId(paymentId: PaymentId): OrderNotFoundError {
            return OrderNotFoundError("Cannot find order with payment id='${paymentId.id}'.")
        }

        fun forCartId(cartId: CartId): OrderNotFoundError {
            return OrderNotFoundError("Cannot find order with cart id='${cartId.id}'.")
        }
    }
}
internal data class CannotRegisterParcelError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): CannotRegisterParcelError {
            return CannotRegisterParcelError("Failed to register parcel for order with id='${id.id()}'")
        }
    }
}

internal data class PackingNotInProgressError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): PackingNotInProgressError {
            return PackingNotInProgressError("Cannot finish packing order with id='${id.id()}' because packing is not started")
        }
    }
}

internal data class CannotRegisterPaymentError(override val message: String) : OrderError(message)  {
    companion object {
        fun forPsp(psp: PaymentServiceProvider): CannotRegisterPaymentError {
            return CannotRegisterPaymentError("Failed to register payment for psp='$psp''")
        }
    }
}
