package pl.szczygieldev.product

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class ProductProjection(val productId:String, val title:String, val description: String, val price: BigDecimal, val priceChanges: List<ProductPriceChangeProjection>, val imageId: UUID){
    data class ProductPriceChangeProjection(val newPrice: BigDecimal, val  timestamp: Instant)
}