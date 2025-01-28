package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.UuidIdentity
import java.util.UUID

internal data class ParcelId(val id: UUID) : UuidIdentity<ParcelId>(id)
