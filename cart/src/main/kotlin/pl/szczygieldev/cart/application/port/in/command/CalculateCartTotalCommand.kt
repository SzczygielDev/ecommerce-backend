package pl.szczygieldev.cart.application.port.`in`.command

import pl.szczygieldev.cart.domain.AppError
import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.ecommercelibrary.command.Command


internal data class CalculateCartTotalCommand(val cartId: CartId) : Command<AppError>()