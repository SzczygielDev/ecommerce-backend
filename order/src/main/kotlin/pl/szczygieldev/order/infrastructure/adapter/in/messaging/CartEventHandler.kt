package pl.szczygieldev.order.infrastructure.adapter.`in`.messaging

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.cart.CartSubmittedEvent
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.order.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.PaymentServiceProvider

@Component
class CartEventHandler(val mediator: Mediator) {

    @EventListener
    suspend fun handleCartEvent(event: CartSubmittedEvent) {
        mediator.send(
            CreateOrderCommand(
                CartId(event.cartId),
                PaymentServiceProvider.valueOf(event.paymentServiceProvider),
                DeliveryProvider.valueOf(event.deliveryProvider)
            )
        )
    }
}