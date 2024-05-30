package pl.szczygieldev.ecommercebackend.domain.error

import pl.szczygieldev.ecommercebackend.domain.ProductId

sealed interface PriceCalculatorError : AppError
data class UnableToCalculateCartTotalError(val message: String) : PriceCalculatorError
data class MissingProductForCalculateError(val message: String, val productId: ProductId) : PriceCalculatorError {
    companion object {
        fun forProduct(productId: ProductId): MissingProductForCalculateError =
            MissingProductForCalculateError(
                "Unable to fetch product with id='${productId.id()}' required to calculate cart total",
                productId
            )
    }
}