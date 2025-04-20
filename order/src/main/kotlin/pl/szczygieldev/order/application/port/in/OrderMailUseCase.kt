package pl.szczygieldev.order.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.SendOrderConfirmationMailCommand

internal interface OrderMailUseCase {
    fun sendConfirmationMail(command: SendOrderConfirmationMailCommand): Either<SendOrderConfirmationMailCommand.Error, Unit>
}