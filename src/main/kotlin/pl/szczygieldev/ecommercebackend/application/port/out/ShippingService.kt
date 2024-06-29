package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.domain.ParcelDimensions
import pl.szczygieldev.ecommercebackend.domain.ParcelIdentifier
import pl.szczygieldev.ecommercebackend.domain.ParcelLabel

interface ShippingService {
    fun registerParcel(parcelDimensions: ParcelDimensions): ParcelIdentifier?

    fun getLabel(parcelIdentifier: ParcelIdentifier): ParcelLabel?
}
