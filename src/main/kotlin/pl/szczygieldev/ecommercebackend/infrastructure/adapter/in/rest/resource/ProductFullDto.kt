package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import java.math.BigDecimal
import java.time.Instant

class ProductFullDto(val productId:String, val title:String, val description: String, val price: BigDecimal, val priceChanges: List<ProductPriceChangeDto>, val imageId: String){
    data class ProductPriceChangeDto(val newPrice: BigDecimal, val  timestamp: Instant)
}