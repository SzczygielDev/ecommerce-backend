package pl.szczygieldev.order.application.port.out

import arrow.core.Either
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.error.AppError

internal interface MailService {
    fun sendOrderConfirmationMail(orderId: OrderId): Either<AppError, Unit>
}