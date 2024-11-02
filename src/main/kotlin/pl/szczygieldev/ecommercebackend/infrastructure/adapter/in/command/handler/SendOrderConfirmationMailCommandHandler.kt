package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderMailUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class SendOrderConfirmationMailCommandHandler(val orderMailUseCase: OrderMailUseCase) :
    CommandWithResultHandler<SendOrderConfirmationMailCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: SendOrderConfirmationMailCommand): Either<AppError, Unit> = either {
        orderMailUseCase.sendConfirmationMail(command).bind()
    }
}