package pl.szczygieldev.shared.ddd.core

interface DomainEventHandler<T : DomainEvent<*>> {
    fun handleEvent(domainEvent: T)
}