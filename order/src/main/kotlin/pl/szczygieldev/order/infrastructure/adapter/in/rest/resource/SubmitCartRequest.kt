package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.PaymentServiceProvider

internal data class SubmitCartRequest(val deliveryProvider: DeliveryProvider, val paymentServiceProvider: PaymentServiceProvider)