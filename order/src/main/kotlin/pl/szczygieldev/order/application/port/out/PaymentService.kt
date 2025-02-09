package pl.szczygieldev.order.application.port.out

import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.domain.PaymentRegistration
import pl.szczygieldev.order.domain.PaymentServiceProvider
import java.math.BigDecimal
import java.net.URL

internal interface PaymentService {
    fun registerPayment(amount: BigDecimal, paymentServiceProvider: PaymentServiceProvider, returnURL: URL): PaymentRegistration?

    fun verifyPayment(paymentId: PaymentId)
}