package pl.szczygieldev.order.infrastructure.mapper

import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pl.szczygieldev.order.domain.PaymentServiceProvider
import pl.szczygieldev.order.domain.PaymentServiceProviderDetails
import java.net.URL

@Component
class PaymentServiceProviderMapper {
    fun mapToDetails(paymentServiceProvider: PaymentServiceProvider): PaymentServiceProviderDetails {
        return when (paymentServiceProvider) {
            PaymentServiceProvider.MOCK_PSP -> PaymentServiceProviderDetails(
                paymentServiceProvider, "Mock Payment Service Provider", URL(
                    "${
                        ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()
                    }/images/payments/mpsp_logo.svg"
                )
            )
        }
    }
}