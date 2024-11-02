package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command
import pl.szczygieldev.ecommercebackend.domain.OrderId

data class SendOrderConfirmationMailCommand(val orderId: OrderId) : Command()