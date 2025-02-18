package pl.szczygieldev.cart.application.port.out

import pl.szczygieldev.cart.CartProjection
import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.cart.domain.UserId

internal interface CartsProjections {
    fun findById(id : CartId): CartProjection?
    fun findActiveForUser(id : UserId): CartProjection?
    fun save(cart: CartProjection)
}