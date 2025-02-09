package pl.szczygieldev.order.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.*
import pl.szczygieldev.order.domain.error.AppError

internal interface OrderUseCase {
    suspend fun createOrder(command: CreateOrderCommand): Either<AppError, Unit>
    suspend fun acceptOrder(command: AcceptOrderCommand): Either<AppError, Unit>
    suspend fun rejectOrder(command: RejectOrderCommand): Either<AppError, Unit>
    suspend fun cancelOrder(command: CancelOrderCommand): Either<AppError, Unit>
    suspend fun returnOrder(command: ReturnOrderCommand): Either<AppError, Unit>
}