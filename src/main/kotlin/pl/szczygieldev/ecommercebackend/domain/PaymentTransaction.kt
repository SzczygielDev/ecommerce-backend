package pl.szczygieldev.ecommercebackend.domain

import java.math.BigDecimal
import java.time.Instant

data class PaymentTransaction(val id: PaymentTransactionId,  val amount: BigDecimal, val timestamp: Instant)