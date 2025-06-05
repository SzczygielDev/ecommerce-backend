package pl.szczygieldev.cart.application.port.out

import pl.szczygieldev.cart.api.CartProjection
import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.cart.domain.ClientId

internal interface CartsProjections {
    fun findById(id : CartId): CartProjection?
    fun findActiveForClient(id : ClientId): CartProjection?
    fun save(cart: CartProjection)
}