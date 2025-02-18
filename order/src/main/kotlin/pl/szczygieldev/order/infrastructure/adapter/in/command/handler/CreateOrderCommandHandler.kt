package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.order.application.port.`in`.OrderUseCase
import pl.szczygieldev.order.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.order.domain.error.AppError

internal class CreateOrderCommandHandler(
    val orderUseCase: OrderUseCase
) : CommandWithResultHandler<CreateOrderCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: CreateOrderCommand): Either<AppError, Unit> = either {
        orderUseCase.createOrder(command).bind()
    }
}