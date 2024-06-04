package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.application.model.CartProjection
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.domain.CartId

@Repository
class CartProjectionRepository : CartsProjections {
    private val db = mutableMapOf<String, CartProjection>()
    override fun findById(id: CartId): CartProjection? = db[id.id]

    override fun save(cart: CartProjection) {
        db[cart.cartId.id] = cart
    }

    override fun findAll(): List<CartProjection> = db.values.toList()
}