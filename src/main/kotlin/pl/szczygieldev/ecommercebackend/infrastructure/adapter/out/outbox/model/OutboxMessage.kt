package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.outbox.model

import java.time.Instant

class OutboxMessage(
    val eventId: String,
    var status: OutboxMessageStatus,
    val eventData: String,
    val eventType: String,
    val timestamp: Instant
)