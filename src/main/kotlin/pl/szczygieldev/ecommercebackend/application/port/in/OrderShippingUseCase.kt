package pl.szczygieldev.ecommercebackend.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CompleteOrderPackingCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface OrderShippingUseCase {
    fun beginPacking(command: BeginOrderPackingCommand): Either<AppError, Unit>
    fun completePacking(command: CompleteOrderPackingCommand): Either<AppError, Unit>
    fun changeDeliveryStatus(command: ChangeOrderDeliveryStatusCommand): Either<AppError, Unit>
}