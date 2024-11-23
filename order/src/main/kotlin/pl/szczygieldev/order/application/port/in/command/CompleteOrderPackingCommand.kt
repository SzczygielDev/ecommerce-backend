package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.application.port.`in`.command.common.CommandId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.ParcelDimensions

data class CompleteOrderPackingCommand (val commandId: CommandId, val orderId: OrderId, val dimensions: ParcelDimensions) : Command(id = commandId)
