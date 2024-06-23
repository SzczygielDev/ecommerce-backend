package pl.szczygieldev.ecommercebackend.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.*
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface OrderUseCase {
    fun createOrder(command: CreateOrderCommand): Either<AppError, Unit>
    fun acceptOrder(command: AcceptOrderCommand): Either<AppError, Unit>
    fun rejectOrder(command: RejectOrderCommand): Either<AppError, Unit>
    fun cancelOrder(command: CancelOrderCommand): Either<AppError, Unit>
    fun returnOrder(command: ReturnOrderCommand): Either<AppError, Unit>
}