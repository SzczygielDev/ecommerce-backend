package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import java.math.BigDecimal

data class ProductDto(val productId:String,val title:String, val description: String,val price: BigDecimal)