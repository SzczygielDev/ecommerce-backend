package pl.szczygieldev.order.application.port.out

import pl.szczygieldev.order.domain.Cart
import pl.szczygieldev.order.domain.CartId

internal interface Carts {
    fun findById(id: CartId): Cart?
}