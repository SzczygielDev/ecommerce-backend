package pl.szczygieldev.product

import org.springframework.stereotype.Component
import pl.szczygieldev.product.application.port.`in`.ProductUseCase
import pl.szczygieldev.product.domain.ProductId
import pl.szczygieldev.product.infrastructure.adapter.out.persistence.ProductRepository

@Component
internal class ProductFacadeImpl(val productRepository: ProductRepository, val productUseCase: ProductUseCase) : ProductFacade {
    override fun findAll(): List<ProductProjection> = productRepository.findAll().map { product ->
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

    override fun findById(id: String): ProductProjection? {
        val product = productRepository.findById(ProductId(id)) ?: return null

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