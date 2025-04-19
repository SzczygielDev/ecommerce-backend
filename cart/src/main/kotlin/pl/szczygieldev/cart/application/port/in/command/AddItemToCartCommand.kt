package pl.szczygieldev.cart.application.port.`in`.command

import pl.szczygieldev.cart.domain.AddToCartError
import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.cart.domain.ClientId
import pl.szczygieldev.cart.domain.ProductId
import pl.szczygieldev.ecommercelibrary.command.CommandError

internal data class AddItemToCartCommand(val clientId: ClientId, val productId: ProductId, val quantity: Int) :
    Command<AddItemToCartCommand.Error>() {
    init {
        require(quantity > 0) { "Item quantity must be positive value, provided='$quantity'" }
    }

    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class CartNotFoundError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forId(id: CartId): CartNotFoundError {
                return CartNotFoundError("Cannot find cart with id='${id.id()}'.", "AC-1")
            }

            fun forClientId(id: ClientId): CartNotFoundError {
                return CartNotFoundError("Cannot find cart for user with id='${id.id()}'.", "AC-1")
            }
        }
    }

    internal class ProductNotFoundError : Error("Product not found", "AC-2")

    internal data class CartNotActiveError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun fromDomainError(domainError: AddToCartError): CartNotActiveError {
                return CartNotActiveError(domainError.message,"AC-3")
            }
        }
    }
}