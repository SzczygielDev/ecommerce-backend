package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.order.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.order.application.port.`in`.command.BeginOrderPackingCommand

internal class BeginOrderPackingCommandHandler(
    val orderShippingUseCase: OrderShippingUseCase,
) :
    CommandWithResultHandler<BeginOrderPackingCommand, Either<BeginOrderPackingCommand.Error, Unit>> {
    override suspend fun handle(command: BeginOrderPackingCommand): Either<BeginOrderPackingCommand.Error, Unit> =
        either {
            orderShippingUseCase.beginPacking(command).bind()
        }
}