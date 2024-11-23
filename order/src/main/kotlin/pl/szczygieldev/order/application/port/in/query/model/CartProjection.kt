package pl.szczygieldev.order.application.port.`in`.query.model

import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.CartStatus
import pl.szczygieldev.order.domain.ProductId
import java.math.BigDecimal

data class CartProjection(val cartId: CartId, val status: CartStatus, val amount: BigDecimal, val items: List<Entry>){
    data class Entry(val productId: ProductId, val quantity: Int)
}