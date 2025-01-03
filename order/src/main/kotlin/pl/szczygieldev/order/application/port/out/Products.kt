package pl.szczygieldev.order.application.port.out

import pl.szczygieldev.order.domain.Product
import pl.szczygieldev.order.domain.ProductId

internal interface Products {
    fun findById(id: ProductId): Product?

    fun findAll(): List<Product>
}