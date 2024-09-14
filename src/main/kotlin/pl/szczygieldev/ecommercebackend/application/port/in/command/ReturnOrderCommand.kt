package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.application.handlers.common.Command
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandId

data class ReturnOrderCommand(val commandId: CommandId, val orderId: OrderId): Command(id = commandId)