package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.command.AcceptOrderCommand
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.application.port.`in`.OrderUseCase

class AcceptOrderCommandHandler(
    val orderUseCase: OrderUseCase
) : CommandWithResultHandler<AcceptOrderCommand, Either<CommandError, Unit>> {

    override suspend fun handle(command: AcceptOrderCommand): Either<CommandError, Unit> = either {
        orderUseCase.acceptOrder(command).bind()
    }
}