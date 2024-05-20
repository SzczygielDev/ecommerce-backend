package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.ProductDto

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
}