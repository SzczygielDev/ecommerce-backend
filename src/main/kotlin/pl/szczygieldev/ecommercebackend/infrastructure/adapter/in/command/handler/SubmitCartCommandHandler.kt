package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class SubmitCartCommandHandler(val cartUseCase: CartUseCase) :
    CommandWithResultHandler<SubmitCartCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: SubmitCartCommand): Either<AppError, Unit> = either {
        cartUseCase.submitCart(command).bind()
    }
}