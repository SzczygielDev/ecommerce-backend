package pl.szczygieldev.cart.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.cart.domain.ClientId
import pl.szczygieldev.cart.domain.ItemRemoveError
import pl.szczygieldev.ecommercelibrary.command.CommandError
import java.util.UUID

internal data class RemoveItemFromCartCommand(val clientId: ClientId, val productId: UUID) :
    Command<RemoveItemFromCartCommand.Error>() {
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class CartNotFoundError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forId(id: CartId): CartNotFoundError {
                return CartNotFoundError("Cannot find cart with id='${id.id()}'.", "RC-1")
            }

            fun forClientId(id: ClientId): CartNotFoundError {
                return CartNotFoundError("Cannot find cart for user with id='${id.id()}'.", "RC-1")
            }
        }
    }

    internal data class CartNotActiveError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun fromDomainError(domainError: ItemRemoveError): CartNotActiveError {
                return CartNotActiveError(domainError.message, "RC-2")
            }
        }
    }
}