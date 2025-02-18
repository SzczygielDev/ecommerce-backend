package pl.szczygieldev.cart.application.port.out

import pl.szczygieldev.cart.domain.Product
import pl.szczygieldev.cart.domain.ProductId

internal interface Products {
    fun findById(id:ProductId): Product?
}