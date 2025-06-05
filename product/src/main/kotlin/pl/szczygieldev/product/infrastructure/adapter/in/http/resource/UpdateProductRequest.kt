package pl.szczygieldev.product.infrastructure.adapter.`in`.http.resource

import java.math.BigDecimal
import java.util.UUID

internal data class UpdateProductRequest(val title: String, val description: String, val price: BigDecimal, val imageId: UUID)