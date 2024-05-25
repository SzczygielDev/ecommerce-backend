package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.application.port.out.Carts
import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import java.util.UUID

@Repository
class CartRepository : Carts {
    private val db = mutableMapOf<String, Cart>()
    override fun nextIdentity(): CartId = CartId.valueOf(UUID.randomUUID().toString())
    override fun findById(id: CartId): Cart? = db[id.id]

    override fun save(cart: Cart) {
        cart.clearOccurredEvents()
        db[cart.cartId.id] = cart
    }
}