package pl.szczygieldev.order.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.order.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.order.application.port.`in`.command.CompleteOrderPackingCommand
import pl.szczygieldev.order.domain.error.AppError

internal interface OrderShippingUseCase {
    suspend fun beginPacking(command: BeginOrderPackingCommand): Either<AppError, Unit>
    suspend fun completePacking(command: CompleteOrderPackingCommand): Either<AppError, Unit>
    suspend fun changeDeliveryStatus(command: ChangeOrderDeliveryStatusCommand): Either<AppError, Unit>
}