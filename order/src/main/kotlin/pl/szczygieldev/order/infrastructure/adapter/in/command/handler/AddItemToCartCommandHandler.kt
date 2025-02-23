package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.CartUseCase
import pl.szczygieldev.order.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.order.domain.error.AppError

internal class AddItemToCartCommandHandler(
    val cartUseCase: CartUseCase
) :
    CommandWithResultHandler<AddItemToCartCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: AddItemToCartCommand): Either<AppError, Unit> = either {
        cartUseCase.addProductToCart(command).bind()
    }
}
