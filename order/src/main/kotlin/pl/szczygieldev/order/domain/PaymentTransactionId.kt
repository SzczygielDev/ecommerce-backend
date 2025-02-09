package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.UuidIdentity
import java.util.UUID


internal data class PaymentTransactionId(val id: UUID): UuidIdentity<PaymentTransactionId>(id)
