package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.model.CartProjection
import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.CartDto
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.CartEntryDto
import java.math.BigDecimal

@Component
class CartPresenter {
    fun toDto(cart: CartProjection): CartDto {
        return CartDto(
            cart.cartId.id, cart.status, cart.items.map { CartEntryDto(it.productId.id(), it.quantity) }.toList(),
            cart.amount
        )
    }
}