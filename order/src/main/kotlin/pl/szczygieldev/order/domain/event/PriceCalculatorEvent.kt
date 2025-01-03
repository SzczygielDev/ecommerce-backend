package pl.szczygieldev.order.domain.event

import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEvent
import pl.szczygieldev.order.domain.CartId
import java.math.BigDecimal

internal sealed class PriceCalculatorEvent : DomainEvent<PriceCalculatorEvent>()

internal class CartTotalRecalculated(val cartId: CartId, val amount: BigDecimal) : PriceCalculatorEvent() {
    override fun toString(): String {
        return "CartTotalRecalculated(id=$id occuredOn=$occurredOn cartId=$cartId, amount=$amount)"
    }
}