package pl.szczygieldev.cart.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.cart.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.cart.domain.AppError

internal interface PriceCalculatorUseCase {
    fun calculateCartTotal(command: CalculateCartTotalCommand) : Either<AppError,Unit>
}