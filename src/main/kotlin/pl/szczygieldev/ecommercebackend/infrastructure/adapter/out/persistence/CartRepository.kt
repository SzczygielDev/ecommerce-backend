package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import pl.szczygieldev.ecommercebackend.application.port.out.Carts
import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.Product
import java.util.UUID

class CartRepository : Carts {
    private val db  = mutableMapOf<String, Cart>()
    override fun nextIdentity(): CartId {
        return CartId.valueOf(UUID.randomUUID().toString())
    }

    override fun findById(id: CartId): Cart? {
       return db[id.id]
    }

    override fun save(cart: Cart) {
        db[cart.cartId.id]=cart
    }
}