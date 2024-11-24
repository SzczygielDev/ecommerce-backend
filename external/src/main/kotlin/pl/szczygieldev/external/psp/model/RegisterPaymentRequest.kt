package pl.szczygieldev.external.psp.model

import java.math.BigDecimal

data class RegisterPaymentRequest(val amount: BigDecimal, val returnUrl: String)