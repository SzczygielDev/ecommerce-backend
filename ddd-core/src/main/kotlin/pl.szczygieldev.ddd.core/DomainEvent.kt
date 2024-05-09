package pl.szczygieldev.ddd.core

import java.time.Instant

interface DomainEvent {

    val occurredOn: Instant

}