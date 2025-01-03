package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.Identity


internal data class ParcelId(val id: String) : Identity<ParcelId>(id)
