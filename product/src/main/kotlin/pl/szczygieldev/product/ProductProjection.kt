package pl.szczygieldev.product

import java.math.BigDecimal
import java.time.Instant

data class ProductProjection(val productId:String, val title:String, val description: String, val price: BigDecimal, val priceChanges: List<ProductPriceChangeProjection>, val imageId: String){
    data class ProductPriceChangeProjection(val newPrice: BigDecimal, val  timestamp: Instant)
}