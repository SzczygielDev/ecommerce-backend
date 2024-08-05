package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.ProductDto
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.ProductFullDto

@Component
class ProductPresenter {
    fun toDto(product: Product): ProductDto {
        return ProductDto(
            product.productId.id(),
            product.title.value,
            product.description.content,
            product.price.amount

        )
    }

    fun toFullDto(product: Product): ProductFullDto {
        return ProductFullDto(
            product.productId.id(),
            product.title.value,
            product.description.content,
            product.price.amount, product.priceChanges.map { priceChange ->
                ProductFullDto.ProductPriceChangeDto(
                    priceChange.newPrice.amount,
                    priceChange.timestamp
                )
            }

        )
    }
}