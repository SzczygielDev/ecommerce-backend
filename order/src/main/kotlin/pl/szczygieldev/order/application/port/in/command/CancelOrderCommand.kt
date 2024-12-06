package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.order.domain.OrderId

data class CancelOrderCommand(val commandId: CommandId, val orderId: OrderId): Command(id = commandId)