package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class AddItemToCartCommandHandler(
    val cartUseCase: CartUseCase
) :
    CommandWithResultHandler<AddItemToCartCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: AddItemToCartCommand): Either<AppError, Unit> = either {
        cartUseCase.addProductToCart(command).bind()
    }
}
