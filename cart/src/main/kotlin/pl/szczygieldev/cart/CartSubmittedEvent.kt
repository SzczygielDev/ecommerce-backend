package pl.szczygieldev.cart

import pl.szczygieldev.ecommercelibrary.messaging.IntegrationEvent
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