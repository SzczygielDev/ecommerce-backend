package pl.szczygieldev.ecommercebackend.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderMailUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.ecommercebackend.application.port.out.MailService
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.shared.architecture.UseCase

@UseCase
class OrderMailService(private val mailService: MailService) : OrderMailUseCase {
    override fun sendConfirmationMail(command: SendOrderConfirmationMailCommand): Either<AppError,Unit> = either {
        mailService.sendOrderConfirmationMail(command.orderId).bind()
    }
}