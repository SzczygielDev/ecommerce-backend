package pl.szczygieldev.order.domain

import java.math.BigDecimal
import java.net.URL

internal data class PaymentDetails(
    val id: PaymentId,
    val amount: BigDecimal,
    val url: URL,
    val paymentServiceProvider: PaymentServiceProvider
)