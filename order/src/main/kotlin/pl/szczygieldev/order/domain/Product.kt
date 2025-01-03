package pl.szczygieldev.order.domain

import java.math.BigDecimal

internal class Product(val productId: ProductId,val title:String, val price:BigDecimal, val imageId: ImageId)