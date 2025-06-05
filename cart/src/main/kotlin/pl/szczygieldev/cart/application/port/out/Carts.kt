package pl.szczygieldev.cart.application.port.out

import pl.szczygieldev.cart.domain.Cart
import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.cart.domain.ClientId

internal interface Carts {
    fun nextIdentity(): CartId
    fun findById(id : CartId): Cart?
    fun save(cart: Cart, version: Int)
    fun findActiveForClient(id : ClientId): Cart?
}