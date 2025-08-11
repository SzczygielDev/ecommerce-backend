package pl.szczygieldev.cart.infrastructure.adapter.`in`.http.resource

import pl.szczygieldev.cart.domain.PaymentServiceProvider

internal data class SubmitCartRequest(val deliveryProvider: String, val paymentServiceProvider: PaymentServiceProvider)