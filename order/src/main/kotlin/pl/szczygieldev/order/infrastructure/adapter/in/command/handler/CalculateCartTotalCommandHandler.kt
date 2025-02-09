package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.PriceCalculatorUseCase
import pl.szczygieldev.order.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.order.domain.error.AppError

internal class CalculateCartTotalCommandHandler(private val priceCalculatorUseCase: PriceCalculatorUseCase) :
    CommandWithResultHandler<CalculateCartTotalCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: CalculateCartTotalCommand): Either<AppError, Unit> = either {
        priceCalculatorUseCase.calculateCartTotal(command).bind()
    }
}

