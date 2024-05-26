package pl.szczygieldev.ddd.core

abstract class AggregateRoot<E : DomainEvent<E>> {
    private val events = mutableListOf<E>()
    fun occurredEvents(): List<E> = events.toList()
    fun clearOccurredEvents() = events.clear()

    var version: Int = 0

    protected abstract fun applyEvent(event: E)

    protected fun raiseEvent(event: E){
        events.add(event)
        applyEvent(event)
    }

    protected fun applyAll(events: List<E>) {
        events.forEach { event ->
            applyEvent(event)
            version++
        }
    }
}