package pl.szczygieldev.ecommercebackend.domain.error

import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.ParcelId
import pl.szczygieldev.ecommercebackend.domain.PaymentId
import java.math.BigDecimal

sealed class OrderError(message: String) : AppError(message)

data class CannotCancelSentOrderError(override val message: String) : OrderError(message) {
    companion object {
        fun forId(id: OrderId): CannotCancelSentOrderError {
            return CannotCancelSentOrderError("Cannot cancel order with id='${id.id()}' because its already sent")
        }
    }
}

data class CannotReturnNotReceivedOrderError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): CannotReturnNotReceivedOrderError {
            return CannotReturnNotReceivedOrderError("Cannot return order with id='${id.id()}' because its not received")
        }
    }
}

data class AlreadyAcceptedOrderError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): AlreadyAcceptedOrderError {
            return AlreadyAcceptedOrderError("Cannot accept or reject order with id='${id.id()}' because it has been already accepted")
        }
    }
}

data class NotPaidOrderError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): NotPaidOrderError {
            return NotPaidOrderError("Cannot process order with id='${id.id()}' because its not paid")
        }
    }
}

data class CannotPackageNotAcceptedOrderError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): CannotPackageNotAcceptedOrderError {
            return CannotPackageNotAcceptedOrderError("Cannot package order with id='${id.id()}' because its not accepted")
        }
    }
}

data class InvalidPaymentAmountError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId, currentAmount: BigDecimal, targetAmount: BigDecimal): InvalidPaymentAmountError {
            return InvalidPaymentAmountError("Invalid payment amount for order with id='${id.id()}' current amount='${currentAmount}' desired amount='${targetAmount}'")
        }
    }
}

data class OrderNotFoundError(override val message: String) : OrderError(message)  {
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
    }
}
data class CannotRegisterParcelError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): CannotRegisterParcelError {
            return CannotRegisterParcelError("Failed to register parcel for order with id='${id.id()}'")
        }
    }
}

data class PackingNotInProgressError(override val message: String) : OrderError(message)  {
    companion object {
        fun forId(id: OrderId): PackingNotInProgressError {
            return PackingNotInProgressError("Cannot finish packing order with id='${id.id()}' because packing is not started")
        }
    }
}
