package pl.szczygieldev.cart.infrastructure.adapter.`in`.http.resource

import java.util.UUID

internal data class AddItemToCartRequest(val productId: UUID, val quantity: Int)