package pl.szczygieldev.ecommercebackend.domain

import java.time.Instant

data class ProductPriceChange(val newPrice: ProductPrice,val  timestamp:Instant)