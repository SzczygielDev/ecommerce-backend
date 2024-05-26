package pl.szczygieldev.ecommercebackend.application.port.`in`

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CalculateCartTotalCommand

interface PriceCalculatorUseCase {
    fun calculateCartTotal(command: CalculateCartTotalCommand)
}