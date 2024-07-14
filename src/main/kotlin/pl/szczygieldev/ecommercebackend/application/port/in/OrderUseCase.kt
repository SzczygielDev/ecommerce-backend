package pl.szczygieldev.ecommercebackend.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.*
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface OrderUseCase {
    suspend fun createOrder(command: CreateOrderCommand): Either<AppError, Unit>
    suspend fun acceptOrder(command: AcceptOrderCommand): Either<AppError, Unit>
    suspend fun rejectOrder(command: RejectOrderCommand): Either<AppError, Unit>
    suspend fun cancelOrder(command: CancelOrderCommand): Either<AppError, Unit>
    suspend fun returnOrder(command: ReturnOrderCommand): Either<AppError, Unit>
}