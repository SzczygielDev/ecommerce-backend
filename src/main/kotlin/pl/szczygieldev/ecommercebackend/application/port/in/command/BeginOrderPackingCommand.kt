package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.CommandId
import pl.szczygieldev.ecommercebackend.domain.OrderId

data class BeginOrderPackingCommand(val commandId: CommandId, val orderId: OrderId) : Command(id = commandId)