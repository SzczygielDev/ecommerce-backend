package pl.szczygieldev.order.infrastructure.integration.shipping

import pl.szczygieldev.order.domain.ParcelLabel

data class Parcel(val id: String, var status: ParcelStatus, val parcelLabel: ParcelLabel, val parcelSize: ParcelSize)