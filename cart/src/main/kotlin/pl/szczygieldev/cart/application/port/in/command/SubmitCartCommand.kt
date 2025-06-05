package pl.szczygieldev.cart.application.port.`in`.command

import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.cart.domain.ClientId
import pl.szczygieldev.cart.domain.DeliveryProvider
import pl.szczygieldev.cart.domain.PaymentServiceProvider
import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError

internal data class SubmitCartCommand(
    val clientId: ClientId,
    val deliveryProvider: DeliveryProvider,
    val paymentServiceProvider: PaymentServiceProvider
) : Command<SubmitCartCommand.Error>() {
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class CartNotFoundError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forId(id: CartId): CartNotFoundError {
                return CartNotFoundError("Cannot find cart with id='${id.id()}'.", "SC-1")
            }

            fun ClientId(id: ClientId): CartNotFoundError {
                return CartNotFoundError("Cannot find cart for user with id='${id.id()}'.","SC-1")
            }
        }
    }

    internal data class CartAlreadySubmittedError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun fromDomainError(domainError: pl.szczygieldev.cart.domain.SubmitError.CartAlreadySubmittedError): CartAlreadySubmittedError {
                return CartAlreadySubmittedError(domainError.message, "SC-2")
            }

        }
    }
}