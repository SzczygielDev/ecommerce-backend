package pl.szczygieldev.order.domain.error

import pl.szczygieldev.order.domain.*


internal sealed class AcceptError(open val message: String){
    internal data class AlreadyAcceptedOrderError(override val message: String) : AcceptError(message)  {
        companion object {
            fun forId(id: OrderId): AlreadyAcceptedOrderError {
                return AlreadyAcceptedOrderError("Cannot accept or reject order with id='${id.id()}' because it has been already accepted")
            }
        }
    }
}

internal sealed class RejectError(open val message: String){
    internal data class AlreadyAcceptedOrderError(override val message: String) : RejectError(message)  {
        companion object {
            fun forId(id: OrderId): AlreadyAcceptedOrderError {
                return AlreadyAcceptedOrderError("Cannot accept or reject order with id='${id.id()}' because it has been already accepted")
            }
        }
    }
}

internal sealed class CancelError(open val message: String){
    internal data class CannotCancelSentOrderError(override val message: String) : CancelError(message) {
        companion object {
            fun forId(id: OrderId): CannotCancelSentOrderError {
                return CannotCancelSentOrderError("Cannot cancel order with id='${id.id()}' because its already sent")
            }
        }
    }
}

internal sealed class ReturnError(open val message: String){
    internal data class CannotReturnNotReceivedOrderError(override val message: String) : ReturnError(message)  {
        companion object {
            fun forId(id: OrderId): CannotReturnNotReceivedOrderError {
                return CannotReturnNotReceivedOrderError("Cannot return order with id='${id.id()}' because its not received")
            }
        }
    }
}

internal sealed class PackingError(open val message: String){
    internal data class CannotPackageNotAcceptedOrderError(override val message: String) : PackingError(message)  {
        companion object {
            fun forId(id: OrderId): CannotPackageNotAcceptedOrderError {
                return CannotPackageNotAcceptedOrderError("Cannot package order with id='${id.id()}' because its not accepted")
            }
        }
    }

    internal data class NotPaidOrderError(override val message: String) : PackingError(message)  {
        companion object {
            fun forId(id: OrderId): NotPaidOrderError {
                return NotPaidOrderError("Cannot process order with id='${id.id()}' because its not paid")
            }
        }
    }
}

internal sealed class CompletePackingError(open val message: String){
    internal data class PackingNotInProgressError(override val message: String) : CompletePackingError(message)  {
        companion object {
            fun forId(id: OrderId): PackingNotInProgressError {
                return PackingNotInProgressError("Cannot finish packing order with id='${id.id()}' because packing is not started")
            }
        }
    }
}