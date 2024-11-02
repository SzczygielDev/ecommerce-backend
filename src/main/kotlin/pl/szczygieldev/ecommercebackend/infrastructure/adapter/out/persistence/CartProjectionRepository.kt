package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.application.port.`in`.query.model.CartProjection
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.CartStatus
import pl.szczygieldev.ecommercebackend.domain.UserId

@Repository
class CartProjectionRepository : CartsProjections {
    private val db = mutableMapOf<String, CartProjection>()
    override fun findById(id: CartId): CartProjection? = db[id.id]
    override fun save(cart: CartProjection) {
        db[cart.cartId.id] = cart
    }

    override fun findAll(): List<CartProjection> = db.values.toList()

    //TODO - replace when implementing users
    override fun findByUser(id: UserId): CartProjection? = db.values.first()

    //TODO - replace when implementing users
    override fun findActiveForUser(id: UserId): CartProjection? =
        db.values.find { cartProjection -> cartProjection.status == CartStatus.ACTIVE }

}