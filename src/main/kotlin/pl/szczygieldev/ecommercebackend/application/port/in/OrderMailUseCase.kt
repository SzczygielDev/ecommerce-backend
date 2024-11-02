package pl.szczygieldev.ecommercebackend.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface OrderMailUseCase {
    fun sendConfirmationMail(command: SendOrderConfirmationMailCommand): Either<AppError, Unit>
}