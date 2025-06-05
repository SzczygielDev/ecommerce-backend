package pl.szczygieldev.order.infrastructure.adapter.out.messaging.mapper

import pl.szczygieldev.ecommercelibrary.outbox.IntegrationEvent
import pl.szczygieldev.ecommercelibrary.outbox.IntegrationEventMapper
import pl.szczygieldev.order.domain.event.OrderEvent

internal class OrderEventMapper : IntegrationEventMapper<OrderEvent> {
    override fun toIntegrationEvent(event: OrderEvent): IntegrationEvent? {
        return null
    }
}