package pl.szczygieldev.external.psp.dto

import java.math.BigDecimal

internal data class PaymentNotificationRequest(val id: String, val paymentId: String, val amount: BigDecimal)