package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.domain.OrderId

data class SendOrderConfirmationMailCommand(val orderId: OrderId) : Command()