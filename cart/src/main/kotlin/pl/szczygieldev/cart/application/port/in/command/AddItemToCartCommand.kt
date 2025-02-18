package pl.szczygieldev.cart.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.cart.domain.AppError
import java.util.UUID


internal data class AddItemToCartCommand(val cartId: UUID, val productId: UUID, val quantity: Int) : Command<AppError>(){
    init {
        require(quantity > 0) { "Item quantity must be positive value, provided='$quantity'" }
    }
}