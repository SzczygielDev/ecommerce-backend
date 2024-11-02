package pl.szczygieldev.ecommercebackend.application.port.out

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface MailService {
    fun sendOrderConfirmationMail(orderId: OrderId): Either<AppError, Unit>
}