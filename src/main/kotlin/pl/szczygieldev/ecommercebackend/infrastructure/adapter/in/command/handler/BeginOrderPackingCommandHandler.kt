package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class BeginOrderPackingCommandHandler(
    val orderShippingUseCase: OrderShippingUseCase,
) :
    CommandWithResultHandler<BeginOrderPackingCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: BeginOrderPackingCommand): Either<AppError, Unit> = either {
        orderShippingUseCase.beginPacking(command).bind()
    }
}