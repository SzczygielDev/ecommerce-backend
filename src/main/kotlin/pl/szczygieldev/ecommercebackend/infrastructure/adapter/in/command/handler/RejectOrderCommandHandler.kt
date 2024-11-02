package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.RejectOrderCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase

class RejectOrderCommandHandler(
    val orderUseCase: OrderUseCase,
) : CommandWithResultHandler<RejectOrderCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: RejectOrderCommand): Either<AppError, Unit> = either {
        orderUseCase.rejectOrder(command).bind()
    }
}