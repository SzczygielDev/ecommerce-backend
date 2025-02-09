package pl.szczygieldev.order.infrastructure.adapter.`in`.api.resource

import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.DeliveryStatus

internal data class DeliveryDto(val deliveryProvider: DeliveryProvider,
                  val status: DeliveryStatus)