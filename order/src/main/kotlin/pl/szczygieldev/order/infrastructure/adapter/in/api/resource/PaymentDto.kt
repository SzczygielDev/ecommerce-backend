package pl.szczygieldev.order.infrastructure.adapter.`in`.api.resource

import pl.szczygieldev.order.domain.PaymentServiceProvider
import pl.szczygieldev.order.domain.PaymentStatus
import java.math.BigDecimal
import java.net.URL

internal data class PaymentDto(val id: String, val amount: BigDecimal, val amountPaid: BigDecimal, val paymentServiceProvider: PaymentServiceProvider, val status: PaymentStatus, val paymentURL: URL)