package pl.szczygieldev.order.domain

import java.math.BigDecimal
import java.time.Instant

internal data class PaymentTransaction(val id: PaymentTransactionId,  val amount: BigDecimal, val timestamp: Instant)