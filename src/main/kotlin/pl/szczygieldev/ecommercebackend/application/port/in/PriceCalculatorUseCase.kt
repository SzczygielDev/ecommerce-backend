package pl.szczygieldev.ecommercebackend.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface PriceCalculatorUseCase {
    fun calculateCartTotal(command: CalculateCartTotalCommand) : Either<AppError,Unit>
}