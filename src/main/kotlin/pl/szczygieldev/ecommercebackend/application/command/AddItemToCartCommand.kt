package pl.szczygieldev.ecommercebackend.application.command


data class AddItemToCartCommand(val cartId: String, val productId: String, val quantity: Int)