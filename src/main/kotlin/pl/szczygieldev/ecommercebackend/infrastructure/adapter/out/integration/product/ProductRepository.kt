package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.integration.product

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.ImageId
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.ProductId
import pl.szczygieldev.product.ProductFacade


@Repository
class ProductRepository(val productFacade: ProductFacade) : Products {
    override fun findById(id: ProductId): Product? {
        val product = productFacade.findById(pl.szczygieldev.product.domain.ProductId(id.id())) ?: return null

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