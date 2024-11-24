package pl.szczygieldev.external.shipping

 import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import pl.szczygieldev.external.shipping.dto.ParcelStatusChangeNotificationRequest
import pl.szczygieldev.external.shipping.model.*
import java.net.URL
import java.util.*

@Service
internal class ShippingService {
    private val db = mutableMapOf<UUID, Parcel>()

    private val notificationUrl = "http://localhost:8080/delivery/notification"
    private val webClient = WebClient.builder()
        .baseUrl(notificationUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun registerParcel(parcelDimensions: ParcelDimensions): UUID {
        val parcelId = UUID.randomUUID()
        val parcel = Parcel(
            parcelId.toString(),
            ParcelStatus.PREPARING,
            generateLabel(),
            ParcelSize(
                parcelDimensions.width,
                parcelDimensions.length,
                parcelDimensions.height,
                parcelDimensions.weight
            )
        )

        db.put(
            parcelId,
            parcel
        )

        return parcelId
    }

    fun getLabel(parcelId: UUID): ParcelLabel? = db.get(parcelId)?.parcelLabel

    private fun generateLabel(): ParcelLabel = ParcelLabel(URL("http://localhost:8080/"))

    @Scheduled(fixedRate = 1000 * 60)
    private fun processParcels() {
        val parcelsToProcess = db.values
        parcelsToProcess.forEach { parcel ->
            if (parcel.status != ParcelStatus.DELIVERED) {
                parcel.status = ParcelStatus.entries.get(parcel.status.ordinal + 1)

                webClient.post()
                    .bodyValue(ParcelStatusChangeNotificationRequest(parcel.id, parcel.status))
                    .retrieve()
                    .toBodilessEntity()
                    .block()
            }
        }
    }
}