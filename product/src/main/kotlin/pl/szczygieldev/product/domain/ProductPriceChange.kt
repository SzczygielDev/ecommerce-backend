package pl.szczygieldev.product.domain

import java.time.Instant

data class ProductPriceChange(val newPrice: ProductPrice, val  timestamp:Instant)