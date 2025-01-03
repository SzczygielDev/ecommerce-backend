package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource

internal data class AddItemToCartRequest(val productId: String, val quantity: Int)