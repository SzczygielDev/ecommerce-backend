package pl.szczygieldev.order.domain

import pl.szczygieldev.shared.ddd.core.Identity

data class ParcelId(val id: String) : Identity<ParcelId>(id)
