package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ddd.core.DomainEventHandler
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.domain.event.CartTotalRecalculated
import pl.szczygieldev.ecommercebackend.domain.event.PriceCalculatorEvent
import pl.szczygieldev.ecommercebackend.domain.exception.CartNotFoundException

@Component
class PriceCalculatorEventHandler(private val cartsProjections: CartsProjections) :
    DomainEventHandler<PriceCalculatorEvent> {
    @EventListener
    override fun handleEvent(domainEvent: PriceCalculatorEvent) {
        when (domainEvent) {
            is CartTotalRecalculated -> {
                val foundCart =
                    cartsProjections.findById(domainEvent.cartId) ?: throw CartNotFoundException(domainEvent.cartId)

                cartsProjections.save(foundCart.copy(amount = domainEvent.amount))
            }
        }
    }
}