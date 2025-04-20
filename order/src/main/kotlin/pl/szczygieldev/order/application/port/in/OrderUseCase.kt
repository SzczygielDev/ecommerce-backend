package pl.szczygieldev.order.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.*

internal interface OrderUseCase {
    suspend fun createOrder(command: CreateOrderCommand): Either<CreateOrderCommand.Error, Unit>
    suspend fun acceptOrder(command: AcceptOrderCommand): Either<AcceptOrderCommand.Error, Unit>
    suspend fun rejectOrder(command: RejectOrderCommand): Either<RejectOrderCommand.Error, Unit>
    suspend fun cancelOrder(command: CancelOrderCommand): Either<CancelOrderCommand.Error, Unit>
    suspend fun returnOrder(command: ReturnOrderCommand): Either<ReturnOrderCommand.Error, Unit>
}