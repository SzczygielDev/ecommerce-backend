package pl.szczygieldev.cart.infrastructure.adapter.`in`.api.resource

import pl.szczygieldev.cart.domain.DeliveryProvider
import pl.szczygieldev.cart.domain.PaymentServiceProvider

internal data class SubmitCartRequest(val deliveryProvider: DeliveryProvider, val paymentServiceProvider: PaymentServiceProvider)