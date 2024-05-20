package pl.szczygieldev.ddd.core
interface DomainEventPublisher<T : DomainEvent<*>> {
    fun publish(domainEvent: T)
}