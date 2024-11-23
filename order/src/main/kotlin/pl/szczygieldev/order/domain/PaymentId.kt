package pl.szczygieldev.order.domain

import pl.szczygieldev.shared.ddd.core.Identity

data class PaymentId(val id: String): Identity<PaymentId>(id)
