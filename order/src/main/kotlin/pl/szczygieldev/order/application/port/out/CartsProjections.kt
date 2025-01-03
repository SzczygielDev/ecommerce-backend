package pl.szczygieldev.order.application.port.out

import pl.szczygieldev.order.application.port.`in`.query.model.CartProjection
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.UserId

internal interface CartsProjections {
    fun findById(id : CartId): CartProjection?
    fun findByUser(id : UserId): CartProjection?
    fun findActiveForUser(id : UserId): CartProjection?
    fun save(cart: CartProjection)
    fun findAll(): List<CartProjection>
}