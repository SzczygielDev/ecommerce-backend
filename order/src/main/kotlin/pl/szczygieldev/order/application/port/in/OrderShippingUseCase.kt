package pl.szczygieldev.order.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.order.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.order.application.port.`in`.command.CompleteOrderPackingCommand

internal interface OrderShippingUseCase {
    suspend fun beginPacking(command: BeginOrderPackingCommand): Either<BeginOrderPackingCommand.Error, Unit>
    suspend fun completePacking(command: CompleteOrderPackingCommand): Either<CompleteOrderPackingCommand.Error, Unit>
    suspend fun changeDeliveryStatus(command: ChangeOrderDeliveryStatusCommand): Either<ChangeOrderDeliveryStatusCommand.Error, Unit>
}