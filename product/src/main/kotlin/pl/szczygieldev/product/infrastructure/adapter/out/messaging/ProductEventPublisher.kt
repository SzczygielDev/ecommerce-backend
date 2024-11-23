package pl.szczygieldev.product.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.product.domain.event.ProductEvent
import pl.szczygieldev.shared.messaging.StoreAndForwardEventPublisher
import pl.szczygieldev.shared.outbox.Outbox

@Component
class ProductEventPublisher(eventPublisher: ApplicationEventPublisher, outbox: Outbox) :
    StoreAndForwardEventPublisher<ProductEvent>(eventPublisher, outbox)