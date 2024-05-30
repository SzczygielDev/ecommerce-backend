package pl.szczygieldev.ecommercebackend.domain.error

import pl.szczygieldev.ecommercebackend.domain.ProductId

sealed interface ProductError : AppError

data class ProductNotFoundError(val message: String) : ProductError {
    companion object {
        fun forId(id: ProductId): ProductNotFoundError {
            return ProductNotFoundError("Cannot find product with id='${id.id()}'")
        }
    }
}