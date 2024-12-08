package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.order.domain.error.AppError

data class RemoveItemFromCartCommand(val cartId: String, val productId: String) : Command<AppError>()