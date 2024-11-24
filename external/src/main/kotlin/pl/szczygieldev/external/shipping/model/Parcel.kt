package pl.szczygieldev.external.shipping.model

internal data class Parcel(val id: String, var status: ParcelStatus, val parcelLabel: ParcelLabel, val parcelSize: ParcelSize)