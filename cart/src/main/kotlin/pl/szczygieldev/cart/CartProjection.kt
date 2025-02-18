package pl.szczygieldev.cart

import java.math.BigDecimal
import java.util.UUID

data class CartProjection(val cartId: UUID, val status: String, val amount: BigDecimal, val items: List<Entry>){
    data class Entry(val productId: UUID, val quantity: Int)
}