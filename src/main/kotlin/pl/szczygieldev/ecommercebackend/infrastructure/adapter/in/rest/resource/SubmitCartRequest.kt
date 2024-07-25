package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.ecommercebackend.domain.DeliveryProvider
import pl.szczygieldev.ecommercebackend.domain.PaymentServiceProvider

data class SubmitCartRequest(val deliveryProvider: DeliveryProvider, val paymentServiceProvider: PaymentServiceProvider)