package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.error.RejectError

internal data class RejectOrderCommand(val commandId: CommandId, val orderId: OrderId): Command<RejectOrderCommand.Error>(id = commandId){
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class AlreadyAcceptedOrderError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun fromDomainError(domainError: RejectError.AlreadyAcceptedOrderError): AlreadyAcceptedOrderError {
                return AlreadyAcceptedOrderError(domainError.message, "RO-1")
            }
        }
    }

  internal data class OrderNotFoundError(override val message: String, override val code: String) :
    Error(message, code)   {
    companion object {
      fun forId(id: OrderId): OrderNotFoundError {
        return OrderNotFoundError("Cannot find order with id='${id.id()}'.","RO-2")
      }
    }
  }
}