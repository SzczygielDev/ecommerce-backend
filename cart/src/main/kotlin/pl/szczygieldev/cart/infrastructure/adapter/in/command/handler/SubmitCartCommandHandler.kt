package pl.szczygieldev.cart.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.CartUseCase
import pl.szczygieldev.cart.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.cart.domain.AppError


internal class SubmitCartCommandHandler(val cartUseCase: CartUseCase) :
    CommandWithResultHandler<SubmitCartCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: SubmitCartCommand): Either<AppError, Unit> = either {
        cartUseCase.submitCart(command).bind()
    }
}