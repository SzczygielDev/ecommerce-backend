package pl.szczygieldev.product.infrastructure.adapter.`in`.api.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.infrastructure.adapter.`in`.api.resource.ProductDto
import pl.szczygieldev.product.infrastructure.adapter.`in`.api.resource.ProductFullDto

@Component
internal class ProductPresenter {
    fun toDto(product: Product): ProductDto {
        return ProductDto(
            product.productId.id(),
            product.title.value,
            product.description.content,
            product.price.amount,
            product.imageId.id()
        )
    }

    fun toFullDto(product: Product): ProductFullDto {
        return ProductFullDto(
            product.productId.id(),
            product.title.value,
            product.description.content,
            product.price.amount,
            product.priceChanges.map { priceChange ->
                ProductFullDto.ProductPriceChangeDto(
                    priceChange.newPrice.amount,
                    priceChange.timestamp
                )
            },
            product.imageId.id()
        )
    }
}