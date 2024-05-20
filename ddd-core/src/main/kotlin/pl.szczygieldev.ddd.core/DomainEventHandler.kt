package pl.szczygieldev.ddd.core

interface DomainEventHandler<T : DomainEvent<*>> {
    fun handleEvent(domainEvent: T)
}