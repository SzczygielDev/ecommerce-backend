package pl.szczygieldev.cart.infrastructure.adapter.out.messaging

import org.springframework.stereotype.Component
import pl.szczygieldev.cart.domain.PriceCalculatorEvent
import pl.szczygieldev.cart.infrastructure.adapter.out.messaging.mapper.PriceCalculatorEventMapper
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.ecommercelibrary.outbox.Outbox
import pl.szczygieldev.ecommercelibrary.outbox.StoreAndForwardEventPublisher

@Component("cartModule.PriceCalculatorEventPublisher")
internal class PriceCalculatorEventPublisher(mediator: Mediator, outbox: Outbox) :
    StoreAndForwardEventPublisher<PriceCalculatorEvent>(mediator, outbox, PriceCalculatorEventMapper())