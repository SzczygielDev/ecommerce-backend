package pl.szczygieldev.ecommercebackend.domain.error

import pl.szczygieldev.ecommercebackend.domain.ProductId


sealed class ProductError(message: String) : AppError(message)

data class ProductNotFoundError(override val message: String) : ProductError(message) {
    companion object {
        fun forId(id: ProductId): ProductNotFoundError {
            return ProductNotFoundError("Cannot find product with id='${id.id()}'")
        }
    }
}