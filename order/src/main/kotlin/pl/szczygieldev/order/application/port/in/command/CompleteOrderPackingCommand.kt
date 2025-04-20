package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.ParcelDimensions
import pl.szczygieldev.order.domain.error.CompletePackingError

internal data class CompleteOrderPackingCommand(
    val commandId: CommandId,
    val orderId: OrderId,
    val dimensions: ParcelDimensions
) : Command<CompleteOrderPackingCommand.Error>(id = commandId) {
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class OrderNotFoundError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forId(id: OrderId): OrderNotFoundError {
                return OrderNotFoundError("Cannot find order with id='${id.id()}'.", "CPO-1")
            }
        }
    }

    internal data class CannotRegisterParcelError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forId(id: OrderId): CannotRegisterParcelError {
                return CannotRegisterParcelError("Failed to register parcel for order with id='${id.id()}'","CPO-2")
            }
        }
    }

    internal data class PackingNotInProgressError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun fromDomainError(domainError: CompletePackingError.PackingNotInProgressError): PackingNotInProgressError {
                return PackingNotInProgressError(domainError.message, "CPO-3")
            }
        }
    }
}
