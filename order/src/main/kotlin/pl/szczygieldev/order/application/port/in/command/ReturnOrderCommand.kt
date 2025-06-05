package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.error.ReturnError

internal data class ReturnOrderCommand(val commandId: CommandId, val orderId: OrderId) :
    Command<ReturnOrderCommand.Error>(id = commandId) {
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class OrderNotFoundError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forId(id: OrderId): OrderNotFoundError {
                return OrderNotFoundError("Cannot find order with id='${id.id()}'.", "REO-1")
            }
        }
    }

    internal data class CannotReturnNotReceivedOrderError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun fromDomainError(domainError: ReturnError.CannotReturnNotReceivedOrderError): CannotReturnNotReceivedOrderError {
                return CannotReturnNotReceivedOrderError(domainError.message, "REO-2")
            }
        }
    }



}