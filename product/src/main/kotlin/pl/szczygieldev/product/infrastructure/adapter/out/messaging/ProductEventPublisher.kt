package pl.szczygieldev.product.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercelibrary.messaging.StoreAndForwardEventPublisher
import pl.szczygieldev.ecommercelibrary.outbox.Outbox
import pl.szczygieldev.product.domain.event.ProductEvent
import pl.szczygieldev.product.infrastructure.adapter.out.messaging.mapper.ProductEventMapper

@Component
internal class ProductEventPublisher(eventPublisher: ApplicationEventPublisher, outbox: Outbox) :
    StoreAndForwardEventPublisher<ProductEvent>(eventPublisher, outbox, ProductEventMapper())