package pl.szczygieldev.order.infrastructure.integration.payments

import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.domain.PaymentTransactionId
import java.math.BigDecimal

data class PaymentNotification(val id: PaymentTransactionId,val paymentId: PaymentId, val amount: BigDecimal)