package pl.szczygieldev.external.psp.dto

import java.math.BigDecimal

internal data class RegisterPaymentRequest(val amount: BigDecimal, val returnUrl: String)