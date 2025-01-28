package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.UuidIdentity
import java.util.UUID


internal data class OrderId (val id: UUID) : UuidIdentity<OrderId>(id)