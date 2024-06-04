package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.domain.CartId

interface Carts {
    fun nextIdentity(): CartId
    fun findById(id : CartId): Cart?
    fun save(cart: Cart, version: Int)
}