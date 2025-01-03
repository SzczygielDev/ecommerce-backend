package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource

import java.math.BigDecimal

internal data class PaymentNotificationRequest(val id: String, val paymentId: String, val amount: BigDecimal)