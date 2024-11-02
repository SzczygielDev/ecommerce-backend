package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CompleteOrderPackingCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class CompleteOrderPackingCommandHandler(
    val orderShippingUseCase: OrderShippingUseCase
) : CommandWithResultHandler<CompleteOrderPackingCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: CompleteOrderPackingCommand): Either<AppError, Unit> = either {
        orderShippingUseCase.completePacking(command).bind()
    }
}