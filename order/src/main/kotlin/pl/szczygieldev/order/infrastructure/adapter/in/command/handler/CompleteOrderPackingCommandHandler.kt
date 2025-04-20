package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.order.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.order.application.port.`in`.command.CompleteOrderPackingCommand

internal class CompleteOrderPackingCommandHandler(
    val orderShippingUseCase: OrderShippingUseCase
) : CommandWithResultHandler<CompleteOrderPackingCommand, Either<CompleteOrderPackingCommand.Error, Unit>> {
    override suspend fun handle(command: CompleteOrderPackingCommand): Either<CompleteOrderPackingCommand.Error, Unit> = either {
        orderShippingUseCase.completePacking(command).bind()
    }
}