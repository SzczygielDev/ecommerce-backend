package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.error.AppError

data class RejectOrderCommand(val commandId: CommandId, val orderId: OrderId): Command<AppError>(id = commandId)