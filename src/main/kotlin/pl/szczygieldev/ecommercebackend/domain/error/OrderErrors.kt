package pl.szczygieldev.ecommercebackend.domain.error

import pl.szczygieldev.ecommercebackend.domain.OrderId
import java.math.BigDecimal

sealed interface OrderError : AppError

data class CannotCancelSentOrderError(val message: String) : OrderError {
    companion object {
        fun forId(id: OrderId): CannotCancelSentOrderError {
            return CannotCancelSentOrderError("Cannot cancel order with id='${id.id()}' because its already sent")
        }
    }
}

data class CannotReturnNotReceivedOrderError(val message: String) : OrderError {
    companion object {
        fun forId(id: OrderId): CannotReturnNotReceivedOrderError {
            return CannotReturnNotReceivedOrderError("Cannot return order with id='${id.id()}' because its not received")
        }
    }
}

data class AlreadyAcceptedOrderError(val message: String) : OrderError {
    companion object {
        fun forId(id: OrderId): AlreadyAcceptedOrderError {
            return AlreadyAcceptedOrderError("Cannot accept or reject order with id='${id.id()}' because it has been already accepted")
        }
    }
}

data class NotPaidOrderError(val message: String) : OrderError {
    companion object {
        fun forId(id: OrderId): NotPaidOrderError {
            return NotPaidOrderError("Cannot process order with id='${id.id()}' because its not paid")
        }
    }
}

data class CannotPackageNotAcceptedOrderError(val message: String) : OrderError {
    companion object {
        fun forId(id: OrderId): CannotPackageNotAcceptedOrderError {
            return CannotPackageNotAcceptedOrderError("Cannot package order with id='${id.id()}' because its not accepted")
        }
    }
}

data class InvalidPaymentAmountError(val message: String) : OrderError {
    companion object {
        fun forId(id: OrderId,currentAmount: BigDecimal, targetAmount: BigDecimal): InvalidPaymentAmountError {
            return InvalidPaymentAmountError("Invalid payment amount for order with id='${id.id()}' current amount='${currentAmount}' desired amount='${targetAmount}'")
        }
    }
}

data class OrderNotFoundError(val message: String) : OrderError {
    companion object {
        fun forId(id: OrderId): OrderNotFoundError {
            return OrderNotFoundError("Cannot find order with id='${id.id()}'.")
        }
    }
}