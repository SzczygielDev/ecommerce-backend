package pl.szczygieldev.ecommercebackend.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.domain.PaymentId
import pl.szczygieldev.ecommercebackend.domain.PaymentTransaction
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface OrderPaymentUseCase {
    fun pay(paymentId: PaymentId,paymentTransaction: PaymentTransaction) : Either<AppError, Unit>
}