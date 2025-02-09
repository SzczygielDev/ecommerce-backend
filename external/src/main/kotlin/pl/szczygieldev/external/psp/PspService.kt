package pl.szczygieldev.external.psp

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import pl.szczygieldev.external.psp.model.Payment
import pl.szczygieldev.external.psp.dto.PaymentNotificationRequest
import pl.szczygieldev.external.psp.model.PaymentStatus
import java.math.BigDecimal
import java.net.URL
import java.util.UUID

@Service
internal class PspService {
    private val db = mutableMapOf<UUID, Payment>()
    private val paymentUrlBase = "http://localhost:64427/mockPayment/"
    private val notificationUrl = "http://localhost:8080/payments/notification"
    private val webClient = WebClient.builder()
        .baseUrl(notificationUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    companion object {
        private val log = KotlinLogging.logger { }
    }


    fun register(amount: BigDecimal, returnUrl : URL): Payment {
        val paymentId = UUID.randomUUID()
        val paymentUrl = URL("$paymentUrlBase${paymentId}")
        val payment = Payment(paymentId.toString(), amount, BigDecimal.ZERO, paymentUrl, PaymentStatus.NOT_PAID, returnUrl)

        db.put(
            paymentId,
            payment
        )

        return payment
    }

    fun verify(paymentId: UUID) {
        log.info { "Payment verified " }
    }

    fun pay(paymentId: UUID, amount: BigDecimal){
        val foundPayment = db[paymentId] ?: throw RuntimeException("Payment for id='${paymentId}' not found!")
        val transactionId = UUID.randomUUID()
        foundPayment.pay(amount)
        webClient.post()
            .bodyValue(PaymentNotificationRequest(transactionId.toString(),foundPayment.id,amount))
            .retrieve()
            .toBodilessEntity()
            .block()
    }

    fun get(paymentId: UUID): Payment? = db[paymentId]

}