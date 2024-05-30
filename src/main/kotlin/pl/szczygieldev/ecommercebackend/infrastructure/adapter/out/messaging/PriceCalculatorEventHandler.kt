package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging

import arrow.core.raise.either
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ddd.core.DomainEventHandler
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.CartTotalRecalculated
import pl.szczygieldev.ecommercebackend.domain.event.PriceCalculatorEvent

@Component
class PriceCalculatorEventHandler(private val cartsProjections: CartsProjections) :
    DomainEventHandler<PriceCalculatorEvent> {
    @EventListener
    override fun handleEvent(domainEvent: PriceCalculatorEvent) = either<AppError,Unit>{
        when (domainEvent) {
            is CartTotalRecalculated -> {
                val foundCart =
                    cartsProjections.findById(domainEvent.cartId)?: raise(CartNotFoundError.forId(domainEvent.cartId))

                cartsProjections.save(foundCart.copy(amount = domainEvent.amount))
            }
        }
    }.fold({
           //TODO
    },{})
}