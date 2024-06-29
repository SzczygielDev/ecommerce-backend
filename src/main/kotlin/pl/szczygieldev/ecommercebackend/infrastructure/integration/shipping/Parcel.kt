package pl.szczygieldev.ecommercebackend.infrastructure.integration.shipping

import pl.szczygieldev.ecommercebackend.domain.ParcelLabel

data class Parcel(val id: String, var status: ParcelStatus, val parcelLabel: ParcelLabel, val parcelSize: ParcelSize)