package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.shared.ddd.core.Identity

data class PaymentTransactionId(val id: String): Identity<PaymentTransactionId>(id)
