package pl.szczygieldev.ecommercebackend.domain

data class Delivery(
    val deliveryProvider: DeliveryProvider,
    val externalParcelIdentifier: String?,
    val status: DeliveryStatus
)