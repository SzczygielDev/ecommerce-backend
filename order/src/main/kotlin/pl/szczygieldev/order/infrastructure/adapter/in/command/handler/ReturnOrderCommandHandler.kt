package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.command.ReturnOrderCommand
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.application.port.`in`.OrderUseCase

internal class ReturnOrderCommandHandler(
    val orderUseCase: OrderUseCase,
) : CommandWithResultHandler<ReturnOrderCommand, Either<AppError, Unit>> {

    override suspend fun handle(command: ReturnOrderCommand): Either<AppError, Unit> = either {
        orderUseCase.returnOrder(command).bind()
    }
}