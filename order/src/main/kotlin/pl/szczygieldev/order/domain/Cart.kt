package pl.szczygieldev.order.domain

import java.math.BigDecimal

internal data class Cart(val id:CartId,val items: List<Item>, val total: BigDecimal){
    data class Item(val productId: ProductId, val quantity: Int)
}