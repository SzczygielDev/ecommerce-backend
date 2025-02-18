package pl.szczygieldev.cart.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.cart.domain.PriceCalculatorEvent
import pl.szczygieldev.cart.infrastructure.adapter.out.messaging.mapper.PriceCalculatorEventMapper
import pl.szczygieldev.ecommercelibrary.messaging.StoreAndForwardEventPublisher
import pl.szczygieldev.ecommercelibrary.outbox.Outbox

@Component("cartModule.PriceCalculatorEventPublisher")
internal class PriceCalculatorEventPublisher(eventPublisher: ApplicationEventPublisher, outbox: Outbox) :
    StoreAndForwardEventPublisher<PriceCalculatorEvent>(eventPublisher, outbox, PriceCalculatorEventMapper())