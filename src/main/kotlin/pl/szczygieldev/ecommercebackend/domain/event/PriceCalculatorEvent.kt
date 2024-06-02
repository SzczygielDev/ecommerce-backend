package pl.szczygieldev.ecommercebackend.domain.event

import pl.szczygieldev.ddd.core.DomainEvent
import pl.szczygieldev.ecommercebackend.domain.CartId
import java.math.BigDecimal
import java.time.Instant

sealed class PriceCalculatorEvent : DomainEvent<PriceCalculatorEvent>()

class CartTotalRecalculated(val cartId: CartId, val amount: BigDecimal) : PriceCalculatorEvent() {
    override fun toString(): String {
        return "CartTotalRecalculated(id=$id occuredOn=$occurredOn cartId=$cartId, amount=$amount)"
    }
}