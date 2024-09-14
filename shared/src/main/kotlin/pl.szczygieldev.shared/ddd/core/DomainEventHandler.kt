package pl.szczygieldev.shared.ddd.core

interface DomainEventHandler<T : DomainEvent<*>> {
    suspend fun handleEvent(domainEvent: T)
}