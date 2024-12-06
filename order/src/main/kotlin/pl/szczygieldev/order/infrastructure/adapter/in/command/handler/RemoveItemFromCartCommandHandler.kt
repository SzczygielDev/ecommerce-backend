package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.CartUseCase
import pl.szczygieldev.order.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.order.domain.error.AppError

class RemoveItemFromCartCommandHandler(val cartUseCase: CartUseCase) :
    CommandWithResultHandler<RemoveItemFromCartCommand, Either<CommandError, Unit>> {
    override suspend fun handle(command: RemoveItemFromCartCommand): Either<CommandError, Unit> = either {
        cartUseCase.removeProductFromCart(command).bind()
    }
}


