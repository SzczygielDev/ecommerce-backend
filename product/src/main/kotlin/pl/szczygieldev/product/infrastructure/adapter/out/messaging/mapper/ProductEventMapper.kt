package pl.szczygieldev.product.infrastructure.adapter.out.messaging.mapper

import pl.szczygieldev.ecommercelibrary.messaging.IntegrationEvent
import pl.szczygieldev.ecommercelibrary.messaging.IntegrationEventMapper
import pl.szczygieldev.product.domain.event.ProductEvent

internal class ProductEventMapper : IntegrationEventMapper<ProductEvent> {
    override fun toIntegrationEvent(event: ProductEvent): IntegrationEvent? {
        return null
    }
}