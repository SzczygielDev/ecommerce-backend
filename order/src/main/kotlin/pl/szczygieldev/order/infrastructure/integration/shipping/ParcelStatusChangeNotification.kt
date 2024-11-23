package pl.szczygieldev.order.infrastructure.integration.shipping

data class ParcelStatusChangeNotification(val parcelId: String, val parcelStatus: ParcelStatus)