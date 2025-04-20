package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.order.application.port.`in`.OrderMailUseCase
import pl.szczygieldev.order.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.order.application.port.out.MailService

@UseCase
internal class OrderMailService(private val mailService: MailService) : OrderMailUseCase {
    override fun sendConfirmationMail(command: SendOrderConfirmationMailCommand): Either<SendOrderConfirmationMailCommand.Error, Unit> =
        either {
            val orderId = command.orderId
            val result = mailService.sendOrderConfirmationMail(orderId)

            result.exceptionOrNull()?.let { error ->
                raise(SendOrderConfirmationMailCommand.MailSendError.forId(orderId, error.message ?: ""))
            }
        }
}