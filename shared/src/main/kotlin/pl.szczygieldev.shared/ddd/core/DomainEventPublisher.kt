package pl.szczygieldev.shared.ddd.core
interface DomainEventPublisher<T : DomainEvent<*>> {
    fun publish(domainEvent: T)
}