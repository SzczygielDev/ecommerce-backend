package pl.szczygieldev.cart.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.CartUseCase
import pl.szczygieldev.cart.application.port.`in`.command.AddItemToCartCommand

internal class AddItemToCartCommandHandler(
    val cartUseCase: CartUseCase
) :
    CommandWithResultHandler<AddItemToCartCommand, Either<AddItemToCartCommand.Error, Unit>> {
    override suspend fun handle(command: AddItemToCartCommand): Either<AddItemToCartCommand.Error, Unit> = either {
        cartUseCase.addProductToCart(command).bind()
    }
}
