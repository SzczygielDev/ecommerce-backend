package pl.szczygieldev.order.infrastructure.adapter.out.integration.payments.model

import java.math.BigDecimal

data class RegisterPaymentRequest(val amount: BigDecimal, val returnUrl: String)