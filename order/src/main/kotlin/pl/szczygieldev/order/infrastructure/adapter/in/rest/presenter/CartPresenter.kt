package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.order.application.port.`in`.query.model.CartProjection
import pl.szczygieldev.order.domain.Cart
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource.CartDto
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource.CartEntryDto
import java.math.BigDecimal

@Component
internal class CartPresenter {
    fun toDto(cart: CartProjection): CartDto {
        return CartDto(
            cart.cartId.id, cart.status, cart.items.map { CartEntryDto(it.productId.id(), it.quantity) }.toList(),
            cart.amount
        )
    }
}