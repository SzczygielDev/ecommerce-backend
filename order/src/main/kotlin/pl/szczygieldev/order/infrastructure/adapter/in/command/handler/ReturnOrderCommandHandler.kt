package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.order.application.port.`in`.command.ReturnOrderCommand
import pl.szczygieldev.order.application.port.`in`.OrderUseCase

internal class ReturnOrderCommandHandler(
    val orderUseCase: OrderUseCase,
) : CommandWithResultHandler<ReturnOrderCommand, Either<ReturnOrderCommand.Error, Unit>> {

    override suspend fun handle(command: ReturnOrderCommand): Either<ReturnOrderCommand.Error, Unit> = either {
        orderUseCase.returnOrder(command).bind()
    }
}