package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.DeliveryStatus

data class DeliveryDto(val deliveryProvider: DeliveryProvider,
                  val status: DeliveryStatus)