package pl.szczygieldev.external.shipping.model

data class ParcelStatusChangeNotificationRequest(val parcelId: String, val parcelStatus: ParcelStatus)