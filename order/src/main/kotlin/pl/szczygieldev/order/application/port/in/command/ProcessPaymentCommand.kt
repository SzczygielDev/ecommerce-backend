package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.domain.PaymentTransaction

internal data class ProcessPaymentCommand(val paymentId: PaymentId, val paymentTransaction: PaymentTransaction) :
    Command<ProcessPaymentCommand.Error>() {
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class OrderNotFoundError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forPaymentId(paymentId: PaymentId): OrderNotFoundError {
                return OrderNotFoundError("Cannot find order with payment id='${paymentId.id}'.","PP-1")
            }
        }
    }
}