package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AcceptOrderCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase

class AcceptOrderCommandHandler(
    val orderUseCase: OrderUseCase
) : CommandWithResultHandler<AcceptOrderCommand, Either<AppError, Unit>> {

    override suspend fun handle(command: AcceptOrderCommand): Either<AppError, Unit> = either {
        orderUseCase.acceptOrder(command).bind()
    }
}