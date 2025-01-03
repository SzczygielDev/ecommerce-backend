package pl.szczygieldev.order.domain

import java.net.URL

internal data class DeliveryProviderDetails(val deliveryProvider: DeliveryProvider, val displayName: String, val logoUrl : URL )