package pl.szczygieldev.order.domain

data class Delivery(
    val deliveryProvider: DeliveryProvider,
    val status: DeliveryStatus,
    val parcel: Parcel?
)