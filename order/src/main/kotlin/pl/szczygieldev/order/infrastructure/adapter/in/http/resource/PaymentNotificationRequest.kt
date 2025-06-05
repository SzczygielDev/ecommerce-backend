package pl.szczygieldev.order.infrastructure.adapter.`in`.http.resource

import java.math.BigDecimal
import java.util.UUID

internal data class PaymentNotificationRequest(val id: UUID, val paymentId: UUID, val amount: BigDecimal)