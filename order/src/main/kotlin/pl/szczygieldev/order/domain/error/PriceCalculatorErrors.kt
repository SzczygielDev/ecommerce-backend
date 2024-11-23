package pl.szczygieldev.order.domain.error

import pl.szczygieldev.order.domain.ProductId

sealed class PriceCalculatorError(message: String) : AppError(message)
data class UnableToCalculateCartTotalError(override val message: String) : PriceCalculatorError(message)
data class MissingProductForCalculateError(override val message: String, val productId: ProductId) : PriceCalculatorError(message) {
    companion object {
        fun forProduct(productId: ProductId): MissingProductForCalculateError =
            MissingProductForCalculateError(
                "Unable to fetch product with id='${productId.id()}' required to calculate cart total",
                productId
            )
    }
}