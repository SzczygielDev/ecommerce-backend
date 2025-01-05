package pl.szczygieldev.product.infrastructure.adapter.`in`.rest.resource

import java.util.UUID

internal data class CreateProductRequest(val title: String, val description: String, val price: Double, val imageId: UUID)
