package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.ParcelDimensions

data class CompleteOrderPackingCommand (val commandId: CommandId, val orderId: OrderId, val dimensions: ParcelDimensions) : Command(id = commandId)
