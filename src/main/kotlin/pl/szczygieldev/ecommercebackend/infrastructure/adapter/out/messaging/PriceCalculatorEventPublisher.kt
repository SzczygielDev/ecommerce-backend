package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.domain.event.PriceCalculatorEvent
import pl.szczygieldev.shared.messaging.StoreAndForwardEventPublisher
import pl.szczygieldev.shared.outbox.Outbox

@Component
class PriceCalculatorEventPublisher(eventPublisher: ApplicationEventPublisher, outbox: Outbox) :
    StoreAndForwardEventPublisher<PriceCalculatorEvent>(eventPublisher, outbox)