package pl.szczygieldev.product

import org.springframework.stereotype.Component
import pl.szczygieldev.product.domain.ProductId
import pl.szczygieldev.product.infrastructure.adapter.out.persistence.ProductRepository

@Component
class ProductFacade(val productRepository: ProductRepository) {
    fun findAll(): List<ProductProjection> = productRepository.findAll().map { product ->
        ProductProjection(
            product.productId.id(),
            product.title.value,
            product.description.content,
            product.price.amount,
            product.priceChanges.map { priceChange ->
                ProductProjection.ProductPriceChangeProjection(
                    priceChange.newPrice.amount,
                    priceChange.timestamp
                )
            },
            product.imageId.id()
        )
    }

    fun findById(id: ProductId): ProductProjection? {
        val product = productRepository.findById(id) ?: return null

        return ProductProjection(
            product.productId.id(),
            product.title.value,
            product.description.content,
            product.price.amount,
            product.priceChanges.map { priceChange ->
                ProductProjection.ProductPriceChangeProjection(
                    priceChange.newPrice.amount,
                    priceChange.timestamp
                )
            },
            product.imageId.id()
        )
    }
}