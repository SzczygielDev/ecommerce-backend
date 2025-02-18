package pl.szczygieldev.order.infrastructure.adapter.out.messaging.mapper

import pl.szczygieldev.ecommercelibrary.messaging.IntegrationEvent
import pl.szczygieldev.ecommercelibrary.messaging.IntegrationEventMapper
import pl.szczygieldev.order.domain.event.OrderEvent

internal class OrderEventMapper : IntegrationEventMapper<OrderEvent> {
    override fun toIntegrationEvent(event: OrderEvent): IntegrationEvent? {
        return null
    }
}