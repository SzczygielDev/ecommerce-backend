package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class CartCreateCommandHandler(
    val cartUseCase: CartUseCase,
) : CommandWithResultHandler<CreateCartCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: CreateCartCommand): Either<AppError, Unit> = either {
        cartUseCase.createCart(command).bind()
    }
}