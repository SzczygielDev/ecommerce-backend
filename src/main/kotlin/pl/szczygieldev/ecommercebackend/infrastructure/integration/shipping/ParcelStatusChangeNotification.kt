package pl.szczygieldev.ecommercebackend.infrastructure.integration.shipping

data class ParcelStatusChangeNotification(val parcelId: String, val parcelStatus: ParcelStatus)