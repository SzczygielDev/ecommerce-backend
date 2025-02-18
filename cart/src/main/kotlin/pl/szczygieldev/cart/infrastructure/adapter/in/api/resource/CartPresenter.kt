package pl.szczygieldev.cart.infrastructure.adapter.`in`.api.resource

import org.springframework.stereotype.Component
import pl.szczygieldev.cart.CartProjection
import pl.szczygieldev.cart.domain.CartStatus

@Component
internal class CartPresenter {
    fun toDto(cart: CartProjection): CartDto {
        return CartDto(
            cart.cartId.toString(),
            CartStatus.valueOf(cart.status),
            cart.items.map { CartEntryDto(it.productId.toString(), it.quantity) }.toList(),
            cart.amount
        )
    }
}