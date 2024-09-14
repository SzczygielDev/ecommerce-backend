package pl.szczygieldev.ecommercebackend.infrastructure.integration.payments

import pl.szczygieldev.ecommercebackend.domain.PaymentId
import pl.szczygieldev.ecommercebackend.domain.PaymentTransactionId
import java.math.BigDecimal

data class PaymentNotification(val id: PaymentTransactionId,val paymentId: PaymentId, val amount: BigDecimal)