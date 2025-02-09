package pl.szczygieldev.external.shipping.dto

import pl.szczygieldev.external.shipping.model.ParcelStatus

internal data class ParcelStatusChangeNotificationRequest(val parcelId: String, val parcelStatus: ParcelStatus)