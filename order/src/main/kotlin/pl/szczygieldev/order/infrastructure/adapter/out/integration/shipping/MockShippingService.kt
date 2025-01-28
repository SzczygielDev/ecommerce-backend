package pl.szczygieldev.order.infrastructure.adapter.out.integration.shipping

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import pl.szczygieldev.order.application.port.out.ShippingService
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.ParcelDimensions
import pl.szczygieldev.order.domain.ParcelId
import pl.szczygieldev.order.domain.ParcelLabel
import pl.szczygieldev.order.infrastructure.adapter.out.integration.shipping.model.ParcelLabelResponse
import pl.szczygieldev.order.infrastructure.adapter.out.integration.shipping.model.RegisterParcelRequest
import pl.szczygieldev.order.infrastructure.adapter.out.integration.shipping.model.RegisterParcelResponse
import java.net.URL
import java.util.*

@Component
internal class MockShippingService : ShippingService {
    private val webClient = WebClient.builder()
        .baseUrl("http://localhost:8080/external/shipping/")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    override fun registerParcel(parcelDimensions: ParcelDimensions, deliveryProvider: DeliveryProvider): ParcelId? {
        return when (deliveryProvider) {
            DeliveryProvider.MOCK_DELIVERY_PROVIDER -> {
                val response = webClient.post()
                    .uri("/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(
                        RegisterParcelRequest(
                            parcelDimensions.width,
                            parcelDimensions.length,
                            parcelDimensions.height,
                            parcelDimensions.weight
                        )
                    )
                    .exchangeToMono<RegisterParcelResponse?> { response ->

                        if (!response.statusCode().is2xxSuccessful) {
                            return@exchangeToMono null
                        }

                        return@exchangeToMono response.bodyToMono<RegisterParcelResponse>()
                    }
                    .onErrorComplete()
                    .block() ?: return null

                ParcelId(UUID.fromString(response.parcelId))
            }
        }
    }

    override fun getLabel(parcelId: ParcelId): ParcelLabel? {
        val response = webClient.get()
            .uri("/label")
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono<ParcelLabelResponse?> { response ->

                if (!response.statusCode().is2xxSuccessful) {
                    return@exchangeToMono null
                }

                return@exchangeToMono response.bodyToMono<ParcelLabelResponse>()
            }
            .onErrorComplete()
            .block() ?: return null

        return ParcelLabel(URL(response.url))
    }
}

