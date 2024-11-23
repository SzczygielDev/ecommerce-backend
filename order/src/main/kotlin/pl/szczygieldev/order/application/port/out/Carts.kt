package pl.szczygieldev.order.application.port.out

import pl.szczygieldev.order.domain.Cart
import pl.szczygieldev.order.domain.CartId

interface Carts {
    fun nextIdentity(): CartId
    fun findById(id : CartId): Cart?
    fun save(cart: Cart, version: Int)
}