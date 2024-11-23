package pl.szczygieldev.order.domain.error

import pl.szczygieldev.order.domain.ProductId


sealed class ProductError(message: String) : AppError(message)

data class ProductNotFoundError(override val message: String) : ProductError(message) {
    companion object {
        fun forId(id: ProductId): ProductNotFoundError {
            return ProductNotFoundError("Cannot find product with id='${id.id()}'")
        }
    }
}