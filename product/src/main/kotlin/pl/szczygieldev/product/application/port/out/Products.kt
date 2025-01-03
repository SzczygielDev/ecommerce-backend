package pl.szczygieldev.product.application.port.out

import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.domain.ProductId

internal interface Products {
    fun nextIdentity(): ProductId

    fun findById(id:ProductId): Product?

    fun findAll(): List<Product>

    fun save(product: Product, version: Int): Product

    fun delete(productId: ProductId): Product?
}