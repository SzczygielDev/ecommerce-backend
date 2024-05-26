package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.ProductId
import java.util.UUID

@Repository
class ProductRepository : Products {
    private val db  = mutableMapOf<String,Product>()
    override fun nextIdentity(): ProductId = ProductId(UUID.randomUUID().toString())
    override fun findById(id: ProductId): Product? = db[id.id()]
    override fun findAll(): List<Product> = db.values.toList()

    override fun save(product: Product) {
        db[product.productId.id()] = product
    }
}