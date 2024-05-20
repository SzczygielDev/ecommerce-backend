package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.application.model.CartProjection
import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.domain.CartId

interface CartsProjections {
    fun findById(id : CartId): CartProjection?
    fun save(cart: CartProjection)
    fun findAll(): List<CartProjection>
}