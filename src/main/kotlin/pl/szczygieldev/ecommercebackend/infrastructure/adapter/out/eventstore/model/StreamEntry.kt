package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.eventstore.model

import pl.szczygieldev.shared.ddd.core.Identity

data class StreamEntry(val aggregateId: Identity<*>, val version: Int, val eventType: String, val eventData: String)
