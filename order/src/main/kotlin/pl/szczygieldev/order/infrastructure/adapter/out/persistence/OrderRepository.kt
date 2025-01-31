package pl.szczygieldev.order.infrastructure.adapter.out.persistence

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercelibrary.eventstore.EventStore
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.domain.Order
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.domain.event.OrderEvent
import pl.szczygieldev.order.infrastructure.adapter.out.persistence.table.OrderLookupTable
import java.util.*

@Repository
internal class OrderRepository(val eventStore: EventStore) : Orders {
    override fun nextIdentity(): OrderId = OrderId(UUID.randomUUID())

    override fun findById(id: OrderId): Order? {
        val eventsForOrder = eventStore.getEvents<OrderEvent>(id) ?: return null

        return Order.fromEvents(id, eventsForOrder)
    }

    override fun save(order: Order, version: Int): Unit = transaction {
        val occurredEvents = order.occurredEvents()
        eventStore.appendEvents(order.orderId, occurredEvents, version)
        order.clearOccurredEvents()

        OrderLookupTable.upsert {
            it[id] = order.orderId.idAsUUID()
            it[paymentId] = order.payment.id.idAsUUID()
        }
    }

    override fun findByPaymentId(paymentId: PaymentId): Order? = transaction {
        val result =
            OrderLookupTable.selectAll().where { OrderLookupTable.paymentId.eq(paymentId.idAsUUID()) }.singleOrNull()
                ?: return@transaction null

        return@transaction findById(OrderId(result[OrderLookupTable.id]))
    }
}