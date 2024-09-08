package pl.szczygieldev.ecommercebackend.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface OrderPaymentUseCase {
    fun pay(command: ProcessPaymentCommand) : Either<AppError, Unit>
}