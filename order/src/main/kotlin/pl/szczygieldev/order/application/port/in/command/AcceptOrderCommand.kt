package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.error.AcceptError

internal data class AcceptOrderCommand(val commandId: CommandId, val orderId: OrderId):  Command<AcceptOrderCommand.Error>(id = commandId){
  internal sealed class Error(override val message: String, override val code: String) :
    CommandError(message, code)

  internal data class AlreadyAcceptedOrderError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun fromDomainError(domainError: AcceptError.AlreadyAcceptedOrderError): AlreadyAcceptedOrderError {
                return AlreadyAcceptedOrderError(domainError.message, "AO-1")
            }
        }
    }

  internal data class OrderNotFoundError(override val message: String, override val code: String) :
    Error(message, code)   {
    companion object {
      fun forId(id: OrderId): OrderNotFoundError {
        return OrderNotFoundError("Cannot find order with id='${id.id()}'.","AO-2")
      }
    }
  }
}