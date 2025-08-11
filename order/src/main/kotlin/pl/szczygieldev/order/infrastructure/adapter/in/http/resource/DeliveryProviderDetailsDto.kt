package pl.szczygieldev.order.infrastructure.adapter.`in`.http.resource

import java.net.URL

data class DeliveryProviderDetailsDto(val deliveryProvider: String, val displayName: String, val logoUrl : URL)