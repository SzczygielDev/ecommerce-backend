package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.application.port.`in`.query.model.CartProjection
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.UserId

interface CartsProjections {
    fun findById(id : CartId): CartProjection?
    fun findByUser(id : UserId): CartProjection?
    fun findActiveForUser(id : UserId): CartProjection?
    fun save(cart: CartProjection)
    fun findAll(): List<CartProjection>
}