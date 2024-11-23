package pl.szczygieldev.order.application.port.`in`.query.model

import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.domain.PaymentServiceProvider
import pl.szczygieldev.order.domain.PaymentStatus
import pl.szczygieldev.order.domain.PaymentTransaction
import java.math.BigDecimal
import java.net.URL

data class PaymentProjection(val paymentId: PaymentId, val amount: BigDecimal, val amountPaid: BigDecimal, val paymentServiceProvider: PaymentServiceProvider, val status: PaymentStatus, val paymentURL: URL, val transactions : List<PaymentTransaction>)