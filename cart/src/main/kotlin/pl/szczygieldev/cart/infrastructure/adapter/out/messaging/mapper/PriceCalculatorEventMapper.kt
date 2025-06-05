package pl.szczygieldev.cart.infrastructure.adapter.out.messaging.mapper

import pl.szczygieldev.cart.domain.CartTotalRecalculated
import pl.szczygieldev.cart.domain.PriceCalculatorEvent
import pl.szczygieldev.ecommercelibrary.outbox.IntegrationEvent
import pl.szczygieldev.ecommercelibrary.outbox.IntegrationEventMapper


internal class PriceCalculatorEventMapper : IntegrationEventMapper<PriceCalculatorEvent> {
    override fun toIntegrationEvent(event: PriceCalculatorEvent): IntegrationEvent? {
        return when (event) {
            is CartTotalRecalculated -> null
        }
    }
}
