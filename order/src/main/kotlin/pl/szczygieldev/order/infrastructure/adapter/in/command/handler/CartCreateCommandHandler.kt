package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.CartUseCase
import pl.szczygieldev.order.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.order.domain.error.AppError

class CartCreateCommandHandler(
    val cartUseCase: CartUseCase,
) : CommandWithResultHandler<CreateCartCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: CreateCartCommand): Either<AppError, Unit> = either {
        cartUseCase.createCart(command).bind()
    }
}