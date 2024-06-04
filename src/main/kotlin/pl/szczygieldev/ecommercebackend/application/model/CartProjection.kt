package pl.szczygieldev.ecommercebackend.application.model

import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.CartStatus
import pl.szczygieldev.ecommercebackend.domain.ProductId
import java.math.BigDecimal

data class CartProjection(val cartId: CartId, val status: CartStatus, val amount: BigDecimal, val items: List<CartProjection.Entry>){
    data class Entry(val productId: ProductId, val quantity: Int)
}