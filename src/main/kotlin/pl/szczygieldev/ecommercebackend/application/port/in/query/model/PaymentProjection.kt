package pl.szczygieldev.ecommercebackend.application.port.`in`.query.model

import pl.szczygieldev.ecommercebackend.domain.PaymentId
import pl.szczygieldev.ecommercebackend.domain.PaymentServiceProvider
import pl.szczygieldev.ecommercebackend.domain.PaymentStatus
import pl.szczygieldev.ecommercebackend.domain.PaymentTransaction
import java.math.BigDecimal
import java.net.URL

data class PaymentProjection(val paymentId: PaymentId, val amount: BigDecimal, val amountPaid: BigDecimal, val paymentServiceProvider: PaymentServiceProvider, val status: PaymentStatus, val paymentURL: URL, val transactions : List<PaymentTransaction>)