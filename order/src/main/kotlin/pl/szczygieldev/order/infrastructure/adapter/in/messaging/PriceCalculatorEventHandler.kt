package pl.szczygieldev.order.infrastructure.adapter.`in`.messaging

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.shared.ddd.core.DomainEventHandler
import pl.szczygieldev.order.application.port.out.CartsProjections
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.CartNotFoundError
import pl.szczygieldev.order.domain.event.CartTotalRecalculated
import pl.szczygieldev.order.domain.event.PriceCalculatorEvent

@Component
class PriceCalculatorEventHandler(private val cartsProjections: CartsProjections) :
    DomainEventHandler<PriceCalculatorEvent> {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @EventListener
    override suspend fun handleEvent(domainEvent: PriceCalculatorEvent) = either<AppError, Unit> {
        when (domainEvent) {
            is CartTotalRecalculated -> {
                val foundCart =
                    cartsProjections.findById(domainEvent.cartId) ?: raise(CartNotFoundError.forId(domainEvent.cartId))

                cartsProjections.save(foundCart.copy(amount = domainEvent.amount))
            }
        }
    }.fold({
        log.error { "Event handling failed=${domainEvent}" }
    }, {
        log.info { "Event handled=${domainEvent}" }
    })
}