package pl.szczygieldev.ddd.core

import java.time.Instant

interface DomainEvent<T : DomainEvent<T>> {

    val occurredOn: Instant

}