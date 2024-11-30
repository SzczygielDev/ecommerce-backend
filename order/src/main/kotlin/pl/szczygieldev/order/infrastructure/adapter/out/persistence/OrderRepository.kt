package pl.szczygieldev.order.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercelibrary.eventstore.EventStore
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.domain.Order
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.event.OrderEvent
import java.util.*

@Repository
class OrderRepository(val eventStore: EventStore) : Orders {
    override fun nextIdentity(): OrderId = OrderId(UUID.randomUUID().toString())

    override fun findById(id: OrderId): Order? {
        val eventsForOrder = eventStore.getEvents<OrderEvent>(id) ?: return null

        return Order.fromEvents(id, eventsForOrder)
    }

    override fun save(order: Order, version: Int) {
        val occurredEvents = order.occurredEvents()
        eventStore.appendEvents(order.orderId, occurredEvents, version)
        order.clearOccurredEvents()
    }
}