package pl.szczygieldev.order.application.port.out

import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.ParcelDimensions
import pl.szczygieldev.order.domain.ParcelId
import pl.szczygieldev.order.domain.ParcelLabel


internal interface ShippingService {
    fun registerParcel(parcelDimensions: ParcelDimensions, deliveryProvider: DeliveryProvider): ParcelId?

    fun getLabel(parcelId: ParcelId): ParcelLabel?
}