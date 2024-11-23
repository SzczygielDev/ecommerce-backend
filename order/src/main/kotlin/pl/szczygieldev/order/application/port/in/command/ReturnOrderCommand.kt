package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.application.port.`in`.command.common.CommandId
import pl.szczygieldev.order.domain.OrderId

data class ReturnOrderCommand(val commandId: CommandId, val orderId: OrderId): Command(id = commandId)