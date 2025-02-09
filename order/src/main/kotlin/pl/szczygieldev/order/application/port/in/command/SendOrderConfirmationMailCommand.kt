package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.error.AppError

internal data class SendOrderConfirmationMailCommand(val orderId: OrderId) : Command<AppError>()