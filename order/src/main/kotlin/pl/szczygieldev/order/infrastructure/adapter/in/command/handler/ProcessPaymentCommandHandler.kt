package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.order.application.port.`in`.OrderPaymentUseCase
import pl.szczygieldev.order.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.order.domain.error.AppError

class ProcessPaymentCommandHandler(val orderPaymentUseCase: OrderPaymentUseCase) : CommandWithResultHandler<ProcessPaymentCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: ProcessPaymentCommand): Either<AppError, Unit> = either {
        orderPaymentUseCase.pay(command).bind()
    }
}

