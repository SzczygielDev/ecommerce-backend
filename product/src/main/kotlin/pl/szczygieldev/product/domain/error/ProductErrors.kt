package pl.szczygieldev.product.domain.error

import pl.szczygieldev.product.domain.ProductId

internal sealed class ProductError(message: String) : AppError(message)

internal data class ProductNotFoundError(override val message: String) : ProductError(message) {
    companion object {
        fun forId(id: ProductId): ProductNotFoundError {
            return ProductNotFoundError("Cannot find product with id='${id.id()}'")
        }
    }
}