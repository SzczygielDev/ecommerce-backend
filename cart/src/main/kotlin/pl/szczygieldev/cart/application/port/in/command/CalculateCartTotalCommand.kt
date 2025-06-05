package pl.szczygieldev.cart.application.port.`in`.command

import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.cart.domain.ProductId
import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError


internal data class CalculateCartTotalCommand(val cartId: CartId) : Command<CalculateCartTotalCommand.Error>() {
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class CartNotFoundError(override val message: String) : Error(message, "CC-1") {
        companion object {
            fun forId(id: CartId): CartNotFoundError {
                return CartNotFoundError("Cannot find cart with id='${id.id()}'.")
            }
        }
    }

    internal data class UnableToCalculateCartTotalError(override val message: String) : Error(message, "PC-2")

    internal data class MissingProductForCalculateError(override val message: String, val productId: ProductId) :
        Error(message, "PC-3") {
        companion object {
            fun forProduct(productId: ProductId): MissingProductForCalculateError =
                MissingProductForCalculateError(
                    "Unable to fetch product with id='${productId.id()}' required to calculate cart total",
                    productId
                )
        }
    }


}