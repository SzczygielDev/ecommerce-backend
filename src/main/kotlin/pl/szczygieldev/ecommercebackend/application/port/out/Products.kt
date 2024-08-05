package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.ProductId

interface Products {
    fun nextIdentity(): ProductId

    fun findById(id:ProductId): Product?

    fun findAll(): List<Product>

    fun save(product: Product, version: Int): Product

    fun delete(productId: ProductId): Product?
}