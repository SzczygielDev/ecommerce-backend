package pl.szczygieldev.order.infrastructure.adapter.`in`.api

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.order.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.domain.PaymentServiceProvider
import pl.szczygieldev.order.domain.PaymentTransaction
import pl.szczygieldev.order.domain.PaymentTransactionId
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.resource.PaymentNotificationRequest
import pl.szczygieldev.order.infrastructure.mapper.PaymentServiceProviderMapper
import java.time.Instant


@RestController
@RequestMapping("/payments")
internal class PaymentController(val paymentServiceProviderMapper: PaymentServiceProviderMapper,val mediator: Mediator) {
    private val logger = KotlinLogging.logger {}
    @GetMapping("/providers")
    fun getPaymentProviders(): ResponseEntity<*>{
        return ResponseEntity.ok(PaymentServiceProvider.values().map { paymentServiceProvider -> paymentServiceProviderMapper.mapToDetails(paymentServiceProvider)  })
    }

    @PostMapping("/notification")
    suspend fun notificationHook(@RequestBody paymentNotification: PaymentNotificationRequest): ResponseEntity<*>{
        logger.info { "Received payment notification='$paymentNotification'" }
        mediator.send(
            ProcessPaymentCommand(
                PaymentId(paymentNotification.paymentId),
                PaymentTransaction(
                    PaymentTransactionId(paymentNotification.id),
                    paymentNotification.amount,
                    Instant.now()
                )
            )
        )
        return ResponseEntity.ok("OK")
    }
}