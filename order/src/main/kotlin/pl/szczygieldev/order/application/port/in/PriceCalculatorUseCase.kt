package pl.szczygieldev.order.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.order.domain.error.AppError

internal interface PriceCalculatorUseCase {
    fun calculateCartTotal(command: CalculateCartTotalCommand) : Either<AppError,Unit>
}