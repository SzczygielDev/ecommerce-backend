package pl.szczygieldev.order.infrastructure.adapter.out.integration.product

import org.springframework.stereotype.Repository
import pl.szczygieldev.order.application.port.out.Products
import pl.szczygieldev.order.domain.ImageId
import pl.szczygieldev.order.domain.Product
import pl.szczygieldev.order.domain.ProductId
import pl.szczygieldev.product.ProductFacade


@Repository
internal class ProductRepository(val productFacade: ProductFacade) : Products {
    override fun findById(id: ProductId): Product? {
        val product = productFacade.findById(id.id()) ?: return null

        return Product(ProductId(product.productId), product.title, product.price, ImageId(product.imageId))
    }

    override fun findAll(): List<Product> = productFacade.findAll().map { product ->
        Product(
            ProductId(product.productId),
            product.title,
            product.price,
            ImageId(product.imageId)
        )
    }
}