package pl.szczygieldev.order.infrastructure.adapter.out.integration.shipping

import org.springframework.context.annotation.Configuration
import pl.szczygieldev.order.application.port.out.ShippingService
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.ParcelDimensions
import pl.szczygieldev.order.domain.ParcelId
import pl.szczygieldev.order.domain.ParcelLabel
import pl.szczygieldev.shipmentsdk.ShippingServiceFactory


@Configuration
internal class ShippingSdkConfig : ShippingService {
    private val shippingSdk = ShippingServiceFactory.create()

    override fun registerParcel(parcelDimensions: ParcelDimensions, deliveryProvider: DeliveryProvider): ParcelId? {
        val parcel = shippingSdk.registerParcel(
            pl.szczygieldev.shipmentsdk.model.ParcelDimensions(
                parcelDimensions.width,
                parcelDimensions.length,
                parcelDimensions.height,
                parcelDimensions.weight,
            ),
            pl.szczygieldev.shipmentsdk.model.DeliveryProvider.valueOf(deliveryProvider.name)
        ) ?: return null

        return ParcelId(parcel.id)
    }

    override fun getLabel(parcelId: ParcelId): ParcelLabel? {
        val parcel = shippingSdk.getLabel(pl.szczygieldev.shipmentsdk.model.ParcelId(parcelId.id)) ?: return null

        return ParcelLabel(parcel.url)
    }
}