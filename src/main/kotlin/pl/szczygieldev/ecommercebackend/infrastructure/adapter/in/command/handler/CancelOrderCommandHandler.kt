package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CancelOrderCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase

class CancelOrderCommandHandler(
    val orderUseCase: OrderUseCase,
) : CommandWithResultHandler<CancelOrderCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: CancelOrderCommand): Either<AppError, Unit> = either {
        orderUseCase.cancelOrder(command).bind()
    }
}