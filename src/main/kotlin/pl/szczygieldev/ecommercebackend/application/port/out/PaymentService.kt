package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.domain.PaymentId
import pl.szczygieldev.ecommercebackend.domain.PaymentRegistration
import pl.szczygieldev.ecommercebackend.domain.PaymentServiceProvider
import java.math.BigDecimal
import java.net.URL

interface PaymentService {
    fun registerPayment(amount: BigDecimal,paymentServiceProvider: PaymentServiceProvider, returnURL: URL): PaymentRegistration

    fun verifyPayment(paymentId: PaymentId)
}