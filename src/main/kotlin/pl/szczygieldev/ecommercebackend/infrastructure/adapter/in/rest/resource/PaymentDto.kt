package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.ecommercebackend.domain.PaymentServiceProvider
import pl.szczygieldev.ecommercebackend.domain.PaymentStatus
import java.math.BigDecimal

data class PaymentDto(val amount: BigDecimal,val amountPaid: BigDecimal, val paymentServiceProvider: PaymentServiceProvider, val status: PaymentStatus)