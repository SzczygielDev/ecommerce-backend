package pl.szczygieldev.order.infrastructure.adapter.out.integration.payments

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import pl.szczygieldev.order.application.port.out.PaymentService
import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.domain.PaymentRegistration
import pl.szczygieldev.order.domain.PaymentServiceProvider
import pl.szczygieldev.order.infrastructure.adapter.out.integration.payments.model.RegisterPaymentRequest
import pl.szczygieldev.order.infrastructure.adapter.out.integration.payments.model.RegisterPaymentResponse
import pl.szczygieldev.order.infrastructure.adapter.out.integration.payments.model.VerifyPaymentRequest
import java.math.BigDecimal
import java.net.URL
import java.util.*

@Component
internal class MockPaymentService(
    @Value("\${app.externalApi.key}")
    private final val externalApiKey: String,
    @Value("\${app.externalApi.psp.url}")
    private final val externalApiUrl: String,
) : PaymentService {

    private val webClient = WebClient.builder()
        .baseUrl(externalApiUrl)
        .defaultHeader("X-API-KEY", externalApiKey)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()


    companion object {
        private val log = KotlinLogging.logger { }
    }

    override fun registerPayment(
        amount: BigDecimal,
        paymentServiceProvider: PaymentServiceProvider,
        returnURL: URL
    ): PaymentRegistration? {
        val result = when (paymentServiceProvider) {
            PaymentServiceProvider.MOCK_PSP -> {
                val response = webClient.post()
                    .uri("/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(RegisterPaymentRequest(amount, returnURL.toString()))
                    .exchangeToMono<RegisterPaymentResponse?> {
                            response ->

                        if(!response.statusCode().is2xxSuccessful){
                            return@exchangeToMono null
                        }

                        return@exchangeToMono response.bodyToMono<RegisterPaymentResponse>()
                    }
                    .onErrorComplete()
                    .block() ?: return null

                val paymentId = PaymentId(UUID.fromString(response.paymentId))
                PaymentRegistration(
                    paymentId,
                    URL(response.paymentUrl),
                )
            }
        }

        return result
    }

    override fun verifyPayment(paymentId: PaymentId) {
        webClient.post()
            .uri("/verify")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(VerifyPaymentRequest(paymentId.id()))
            .retrieve()
            .toBodilessEntity()
            .block()
    }
}