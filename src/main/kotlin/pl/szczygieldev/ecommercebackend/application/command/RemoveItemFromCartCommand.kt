package pl.szczygieldev.ecommercebackend.application.command

data class RemoveItemFromCartCommand(val cartId: String, val productId: String)