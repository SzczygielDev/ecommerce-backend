package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

data class AddItemToCartRequest(val cartId: String, val productId: String, val quantity: Int)