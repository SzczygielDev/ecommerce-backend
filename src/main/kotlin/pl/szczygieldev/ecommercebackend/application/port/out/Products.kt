package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.ProductId

interface Products {
    fun findById(id: ProductId): Product?

    fun findAll(): List<Product>
}