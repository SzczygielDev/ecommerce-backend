package pl.szczygieldev.order.domain

internal data class Delivery(
    val deliveryProvider: DeliveryProvider,
    val status: DeliveryStatus,
    val parcel: Parcel?
)