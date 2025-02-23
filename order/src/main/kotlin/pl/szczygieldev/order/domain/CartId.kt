package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.UuidIdentity
import java.util.UUID

internal data class CartId (val id:UUID): UuidIdentity<CartId>(id)