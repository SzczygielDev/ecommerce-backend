package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource

data class ParcelStatusChangeNotificationRequest(val parcelId: String, val parcelStatus: ParcelStatus)