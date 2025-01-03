package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.Identity


internal data class PaymentId(val id: String): Identity<PaymentId>(id)
