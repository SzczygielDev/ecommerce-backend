package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.order.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.order.domain.error.AppError

class BeginOrderPackingCommandHandler(
    val orderShippingUseCase: OrderShippingUseCase,
) :
    CommandWithResultHandler<BeginOrderPackingCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: BeginOrderPackingCommand): Either<AppError, Unit> = either {
        orderShippingUseCase.beginPacking(command).bind()
    }
}