package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.domain.OrderId

internal data class SendOrderConfirmationMailCommand(val orderId: OrderId) :
    Command<SendOrderConfirmationMailCommand.Error>() {
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class MailSendError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forId(id: OrderId, cause: String): MailSendError {
                return MailSendError(
                    "Failed to send confirmation mail for order with id='${id.id()}', cause='$cause'.",
                    "SM-1"
                )
            }
        }
    }
}