package pl.szczygieldev.ecommercebackend.domain

data class Delivery(
    val deliveryProvider: DeliveryProvider,
    val status: DeliveryStatus,
    val parcel: Parcel?
)