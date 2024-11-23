package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.order.application.port.`in`.OrderMailUseCase
import pl.szczygieldev.order.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.order.application.port.out.MailService
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.shared.architecture.UseCase

@UseCase
class OrderMailService(private val mailService: MailService) : OrderMailUseCase {
    override fun sendConfirmationMail(command: SendOrderConfirmationMailCommand): Either<AppError,Unit> = either {
        mailService.sendOrderConfirmationMail(command.orderId).bind()
    }
}