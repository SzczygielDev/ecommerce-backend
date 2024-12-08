package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.error.AppError

data class CalculateCartTotalCommand(val cartId: CartId) : Command<AppError>()