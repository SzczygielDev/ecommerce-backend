package pl.szczygieldev.ecommercebackend.application.port.`in`.command

data class RemoveItemFromCartCommand(val cartId: String, val productId: String)