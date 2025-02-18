package pl.szczygieldev.cart.infrastructure.adapter.out.integration.product

import org.springframework.stereotype.Repository
import pl.szczygieldev.cart.application.port.out.Products
import pl.szczygieldev.cart.domain.Product
import pl.szczygieldev.cart.domain.ProductId
import pl.szczygieldev.product.ProductFacade
import java.util.UUID

@Repository("cartModule.ProductRepository")
internal class ProductRepository(val productFacade: ProductFacade) : Products {
    override fun findById(id: ProductId): Product? {
        val found = productFacade.findById(id.id) ?: return null

        return Product(ProductId(UUID.fromString(found.productId)),found.title,found.price)
    }
}