package pl.szczygieldev.cart.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.CartUseCase
import pl.szczygieldev.cart.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.cart.domain.AppError

internal class RemoveItemFromCartCommandHandler(val cartUseCase: CartUseCase) :
    CommandWithResultHandler<RemoveItemFromCartCommand, Either<RemoveItemFromCartCommand.Error, Unit>> {
    override suspend fun handle(command: RemoveItemFromCartCommand): Either<RemoveItemFromCartCommand.Error, Unit> = either {
        cartUseCase.removeProductFromCart(command).bind()
    }
}


