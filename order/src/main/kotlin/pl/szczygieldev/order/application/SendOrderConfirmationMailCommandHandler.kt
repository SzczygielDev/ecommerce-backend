package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.order.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.order.application.port.out.MailService

internal class SendOrderConfirmationMailCommandHandler(private val mailService: MailService) :
    CommandWithResultHandler<SendOrderConfirmationMailCommand, Either<SendOrderConfirmationMailCommand.Error, Unit>> {
    override suspend fun handle(command: SendOrderConfirmationMailCommand): Either<SendOrderConfirmationMailCommand.Error, Unit> = either {
        val orderId = command.orderId
        val result = mailService.sendOrderConfirmationMail(orderId)

        result.exceptionOrNull()?.let { error ->
            raise(SendOrderConfirmationMailCommand.MailSendError.forId(orderId, error.message ?: ""))
        }
    }
}