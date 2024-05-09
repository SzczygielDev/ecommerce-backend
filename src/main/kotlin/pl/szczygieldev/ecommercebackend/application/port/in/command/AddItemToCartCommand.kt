package pl.szczygieldev.ecommercebackend.application.port.`in`.command


data class AddItemToCartCommand(val cartId: String, val productId: String, val quantity: Int)