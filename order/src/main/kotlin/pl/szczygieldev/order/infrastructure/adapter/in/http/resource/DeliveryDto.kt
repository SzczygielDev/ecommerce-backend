package pl.szczygieldev.order.infrastructure.adapter.`in`.http.resource

import pl.szczygieldev.order.domain.DeliveryStatus

internal data class DeliveryDto(
    val deliveryProvider: String,
    val status: DeliveryStatus
)