package pl.szczygieldev.order.infrastructure.adapter.`in`.api.resource

import java.util.UUID

internal data class AddItemToCartRequest(val productId: UUID, val quantity: Int)