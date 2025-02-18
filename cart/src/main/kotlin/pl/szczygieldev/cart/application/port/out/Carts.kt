package pl.szczygieldev.cart.application.port.out

import pl.szczygieldev.cart.domain.Cart
import pl.szczygieldev.cart.domain.CartId

internal interface Carts {
    fun nextIdentity(): CartId
    fun findById(id : CartId): Cart?
    fun save(cart: Cart, version: Int)
}