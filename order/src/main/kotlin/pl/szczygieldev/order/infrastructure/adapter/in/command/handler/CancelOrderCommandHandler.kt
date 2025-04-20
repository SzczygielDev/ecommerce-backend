package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.order.application.port.`in`.command.CancelOrderCommand
import pl.szczygieldev.order.application.port.`in`.OrderUseCase

internal class CancelOrderCommandHandler(
    val orderUseCase: OrderUseCase,
) : CommandWithResultHandler<CancelOrderCommand, Either<CancelOrderCommand.Error, Unit>> {
    override suspend fun handle(command: CancelOrderCommand): Either<CancelOrderCommand.Error, Unit> = either {
        orderUseCase.cancelOrder(command).bind()
    }
}