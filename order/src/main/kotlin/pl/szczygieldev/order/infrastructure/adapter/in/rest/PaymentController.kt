package pl.szczygieldev.order.infrastructure.adapter.`in`.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.order.domain.PaymentServiceProvider
import pl.szczygieldev.order.infrastructure.mapper.PaymentServiceProviderMapper


@RestController
@RequestMapping("/payments")
class PaymentController(val paymentServiceProviderMapper: PaymentServiceProviderMapper) {

    @GetMapping("/providers")
    fun getPaymentProviders(): ResponseEntity<*>{
        return ResponseEntity.ok(PaymentServiceProvider.values().map { paymentServiceProvider -> paymentServiceProviderMapper.mapToDetails(paymentServiceProvider)  })
    }
}