package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.domain.Order
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.eventstore.EventStore
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