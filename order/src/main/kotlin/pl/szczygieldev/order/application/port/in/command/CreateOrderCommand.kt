package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.PaymentServiceProvider

internal data class CreateOrderCommand(
    val cartId: CartId,
    val paymentServiceProvider: PaymentServiceProvider,
    val deliveryProvider: DeliveryProvider
) : Command<CreateOrderCommand.Error>() {
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal class CartNotFoundError : Error("Cart not found", "CO-1")

    internal data class CannotRegisterPaymentError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forPsp(psp: PaymentServiceProvider): CannotRegisterPaymentError {
                return CannotRegisterPaymentError("Failed to register payment for psp='$psp''","CO-2")
            }
        }
    }
}