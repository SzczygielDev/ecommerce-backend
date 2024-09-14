package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.domain.DeliveryProvider
import pl.szczygieldev.ecommercebackend.domain.ParcelDimensions
import pl.szczygieldev.ecommercebackend.domain.ParcelId
import pl.szczygieldev.ecommercebackend.domain.ParcelLabel

interface ShippingService {
    fun registerParcel(parcelDimensions: ParcelDimensions, deliveryProvider: DeliveryProvider): ParcelId?

    fun getLabel(parcelId: ParcelId): ParcelLabel?
}