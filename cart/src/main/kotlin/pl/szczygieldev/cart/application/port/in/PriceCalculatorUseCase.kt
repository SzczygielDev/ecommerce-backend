package pl.szczygieldev.cart.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.cart.application.port.`in`.command.CalculateCartTotalCommand

internal interface PriceCalculatorUseCase {
    fun calculateCartTotal(command: CalculateCartTotalCommand) : Either<CalculateCartTotalCommand.Error,Unit>
}