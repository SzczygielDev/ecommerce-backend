package pl.szczygieldev.cart.api

import pl.szczygieldev.ecommercelibrary.outbox.IntegrationEvent
import java.time.Instant
import java.util.*

class CartSubmittedEvent(
    id: UUID,
    occurredOn: Instant,
    val cartId: UUID,
    val paymentServiceProvider: String,
    val deliveryProvider: String
) :
    IntegrationEvent(id, occurredOn)