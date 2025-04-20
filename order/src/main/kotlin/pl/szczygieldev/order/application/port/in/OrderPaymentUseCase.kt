package pl.szczygieldev.order.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.ProcessPaymentCommand

internal interface OrderPaymentUseCase {
    suspend fun pay(command: ProcessPaymentCommand): Either<ProcessPaymentCommand.Error, Unit>
}