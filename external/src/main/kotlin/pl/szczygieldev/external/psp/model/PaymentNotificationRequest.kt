package pl.szczygieldev.external.psp.model

import java.math.BigDecimal

data class PaymentNotificationRequest(val id: String, val paymentId: String, val amount: BigDecimal)