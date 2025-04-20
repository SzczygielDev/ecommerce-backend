package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.order.application.port.`in`.OrderMailUseCase
import pl.szczygieldev.order.application.port.`in`.command.SendOrderConfirmationMailCommand

internal class SendOrderConfirmationMailCommandHandler(val orderMailUseCase: OrderMailUseCase) :
    CommandWithResultHandler<SendOrderConfirmationMailCommand, Either<SendOrderConfirmationMailCommand.Error, Unit>> {
    override suspend fun handle(command: SendOrderConfirmationMailCommand): Either<SendOrderConfirmationMailCommand.Error, Unit> = either {
        orderMailUseCase.sendConfirmationMail(command).bind()
    }
}