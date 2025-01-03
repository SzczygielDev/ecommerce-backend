package pl.szczygieldev.product.infrastructure.adapter.`in`.rest.resource

internal data class CreateProductRequest(val title: String, val description: String, val price: Double, val imageId: String)
