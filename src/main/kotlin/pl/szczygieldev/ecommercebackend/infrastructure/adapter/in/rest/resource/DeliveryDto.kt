package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.ecommercebackend.domain.DeliveryProvider
import pl.szczygieldev.ecommercebackend.domain.DeliveryStatus

data class DeliveryDto(val deliveryProvider: DeliveryProvider,
                  val status: DeliveryStatus)