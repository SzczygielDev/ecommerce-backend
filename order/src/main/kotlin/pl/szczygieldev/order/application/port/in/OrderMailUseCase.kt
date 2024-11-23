package pl.szczygieldev.order.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.order.domain.error.AppError

interface OrderMailUseCase {
    fun sendConfirmationMail(command: SendOrderConfirmationMailCommand): Either<AppError, Unit>
}