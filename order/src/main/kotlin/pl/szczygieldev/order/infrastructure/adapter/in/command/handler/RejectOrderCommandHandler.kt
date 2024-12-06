package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.command.RejectOrderCommand
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.application.port.`in`.OrderUseCase

class RejectOrderCommandHandler(
    val orderUseCase: OrderUseCase,
) : CommandWithResultHandler<RejectOrderCommand, Either<CommandError, Unit>> {
    override suspend fun handle(command: RejectOrderCommand): Either<CommandError, Unit> = either {
        orderUseCase.rejectOrder(command).bind()
    }
}