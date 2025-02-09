package pl.szczygieldev.order.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.order.domain.error.AppError

internal interface OrderPaymentUseCase {
    fun pay(command: ProcessPaymentCommand) : Either<AppError, Unit>
}