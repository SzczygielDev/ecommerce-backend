package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource

import java.math.BigDecimal

data class PaymentNotificationRequest(val id: String, val paymentId: String, val amount: BigDecimal)