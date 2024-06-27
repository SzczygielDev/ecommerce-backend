package pl.szczygieldev.ecommercebackend.application.model

import pl.szczygieldev.ecommercebackend.domain.PaymentServiceProvider
import pl.szczygieldev.ecommercebackend.domain.PaymentStatus
import java.math.BigDecimal

data class PaymentProjection(val amount: BigDecimal, val paymentServiceProvider: PaymentServiceProvider, val status: PaymentStatus)