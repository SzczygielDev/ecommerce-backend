package pl.szczygieldev.cart.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.CartUseCase
import pl.szczygieldev.cart.application.port.`in`.command.CreateCartCommand

internal class CartCreateCommandHandler(
    val cartUseCase: CartUseCase,
) : CommandWithResultHandler<CreateCartCommand, Either<CreateCartCommand.Error, Unit>> {
    override suspend fun handle(command: CreateCartCommand): Either<CreateCartCommand.Error, Unit> = either {
        cartUseCase.createCart(command).bind()
    }
}