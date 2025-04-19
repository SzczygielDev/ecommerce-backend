package pl.szczygieldev.cart.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.PriceCalculatorUseCase
import pl.szczygieldev.cart.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.cart.domain.AppError

internal class CalculateCartTotalCommandHandler(private val priceCalculatorUseCase: PriceCalculatorUseCase) :
    CommandWithResultHandler<CalculateCartTotalCommand, Either<CalculateCartTotalCommand.Error, Unit>> {
    override suspend fun handle(command: CalculateCartTotalCommand): Either<CalculateCartTotalCommand.Error, Unit> = either {
        priceCalculatorUseCase.calculateCartTotal(command).bind()
    }
}

