package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.order.domain.error.AppError


data class AddItemToCartCommand(val cartId: String, val productId: String, val quantity: Int) : Command<AppError>(){
    init {
        require(quantity > 0) { "Item quantity must be positive value, provided='$quantity'" }
    }
}