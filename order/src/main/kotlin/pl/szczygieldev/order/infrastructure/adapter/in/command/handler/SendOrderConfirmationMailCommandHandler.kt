package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.OrderMailUseCase
import pl.szczygieldev.order.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.order.domain.error.AppError

class SendOrderConfirmationMailCommandHandler(val orderMailUseCase: OrderMailUseCase) :
    CommandWithResultHandler<SendOrderConfirmationMailCommand, Either<CommandError, Unit>> {
    override suspend fun handle(command: SendOrderConfirmationMailCommand): Either<CommandError, Unit> = either {
        orderMailUseCase.sendConfirmationMail(command).bind()
    }
}