package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.error.PackingError


internal data class BeginOrderPackingCommand(val commandId: CommandId, val orderId: OrderId) :
    Command<BeginOrderPackingCommand.Error>(id = commandId) {
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class OrderNotFoundError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forId(id: OrderId): OrderNotFoundError {
                return OrderNotFoundError("Cannot find order with id='${id.id()}'.", "BPO-1")
            }
        }
    }

    internal data class CannotPackageNotAcceptedOrderError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun fromDomainError(domainError: PackingError.CannotPackageNotAcceptedOrderError): CannotPackageNotAcceptedOrderError {
                return CannotPackageNotAcceptedOrderError(domainError.message, "BPO-2")
            }
        }
    }

    internal data class NotPaidOrderError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun fromDomainError(domainError: PackingError.NotPaidOrderError): NotPaidOrderError {
                return NotPaidOrderError(domainError.message, "BPO-3")
            }
        }
    }
}