package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.ProductId
import java.util.UUID

class ProductRepository : Products {
    private val db  = mutableMapOf<String,Product>()
    override fun nextIdentity(): ProductId {
        return ProductId.valueOf(UUID.randomUUID().toString())
    }

    override fun findById(id: ProductId): Product? {
        return db[id.id()]
    }

    override fun save(product: Product) {
        db[product.productId.id()] = product
    }
}