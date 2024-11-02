package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command


data class AddItemToCartCommand(val cartId: String, val productId: String, val quantity: Int) : Command(){
    init {
        require(quantity > 0) { "Item quantity must be positive value, provided='$quantity'" }
    }
}