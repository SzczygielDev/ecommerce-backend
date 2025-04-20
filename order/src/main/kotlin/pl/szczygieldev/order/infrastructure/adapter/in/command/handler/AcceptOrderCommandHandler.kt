package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.order.application.port.`in`.command.AcceptOrderCommand
import pl.szczygieldev.order.application.port.`in`.OrderUseCase

internal class AcceptOrderCommandHandler(
    val orderUseCase: OrderUseCase
) : CommandWithResultHandler<AcceptOrderCommand, Either<AcceptOrderCommand.Error, Unit>> {

    override suspend fun handle(command: AcceptOrderCommand): Either<AcceptOrderCommand.Error, Unit> = either {
        orderUseCase.acceptOrder(command).bind()
    }
}