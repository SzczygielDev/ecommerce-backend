package pl.szczygieldev.product.infrastructure.adapter.`in`.api.resource

import java.math.BigDecimal

internal data class ProductDto(val productId:String,val title:String, val description: String,val price: BigDecimal, val imageId: String)