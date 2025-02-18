package pl.szczygieldev.cart.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.CartUseCase
import pl.szczygieldev.cart.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.cart.domain.AppError

internal class CartCreateCommandHandler(
    val cartUseCase: CartUseCase,
) : CommandWithResultHandler<CreateCartCommand, Either<pl.szczygieldev.cart.domain.AppError, Unit>> {
    override suspend fun handle(command: CreateCartCommand): Either<AppError, Unit> = either {
        cartUseCase.createCart(command).bind()
    }
}