package pl.szczygieldev.order.infrastructure.mapper

import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.DeliveryProviderDetails
import java.net.URL

@Component
internal class DeliveryProviderMapper {
    fun mapToDetails(deliveryProvider: DeliveryProvider): DeliveryProviderDetails {
        return when (deliveryProvider) {
            DeliveryProvider.MOCK_DELIVERY_PROVIDER -> DeliveryProviderDetails(
                deliveryProvider,
                "Mock Delivery Provider",
                URL(
                    "${
                        ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()
                    }/images/shipping/mdp_logo.svg"
                )
            )
        }
    }
}