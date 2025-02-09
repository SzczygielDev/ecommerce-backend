package pl.szczygieldev.order.infrastructure.adapter.`in`.api.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.order.application.port.`in`.query.model.CartProjection
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.resource.CartDto
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.resource.CartEntryDto

@Component
internal class CartPresenter {
    fun toDto(cart: CartProjection): CartDto {
        return CartDto(
            cart.cartId.id.toString(), cart.status, cart.items.map { CartEntryDto(it.productId.id(), it.quantity) }.toList(),
            cart.amount
        )
    }
}