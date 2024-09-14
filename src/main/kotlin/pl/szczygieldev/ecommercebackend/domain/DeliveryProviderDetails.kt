package pl.szczygieldev.ecommercebackend.domain

import java.net.URL

data class DeliveryProviderDetails(val deliveryProvider: DeliveryProvider, val displayName: String, val logoUrl : URL )