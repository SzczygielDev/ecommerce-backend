package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.Identity


data class ParcelId(val id: String) : Identity<ParcelId>(id)
