package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.command.CancelOrderCommand
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.application.port.`in`.OrderUseCase

class CancelOrderCommandHandler(
    val orderUseCase: OrderUseCase,
) : CommandWithResultHandler<CancelOrderCommand, Either<CommandError, Unit>> {
    override suspend fun handle(command: CancelOrderCommand): Either<CommandError, Unit> = either {
        orderUseCase.cancelOrder(command).bind()
    }
}