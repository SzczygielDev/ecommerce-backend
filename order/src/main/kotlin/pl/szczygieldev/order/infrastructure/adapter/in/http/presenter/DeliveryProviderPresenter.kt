package pl.szczygieldev.order.infrastructure.adapter.`in`.http.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.order.infrastructure.adapter.`in`.http.resource.DeliveryProviderDetailsDto
import pl.szczygieldev.shipmentsdk.model.DeliveryProvider

@Component
internal class DeliveryProviderPresenter {
    fun toDto(deliveryProvider: DeliveryProvider): DeliveryProviderDetailsDto {
        return DeliveryProviderDetailsDto(
            deliveryProvider.providerName,
            deliveryProvider.displayName,
            deliveryProvider.logoUrl
        )
    }
}