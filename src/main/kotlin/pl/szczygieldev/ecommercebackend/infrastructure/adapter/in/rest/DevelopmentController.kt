package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercebackend.application.model.OrderProjection
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.domain.PaymentId
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter.CommandPresenter
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter.OrderPresenter
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.integration.payments.MockPaymentService
import java.math.BigDecimal

@RequestMapping("/development")
@RestController
class DevelopmentController(
    val commandResultStorage: CommandResultStorage,
    val commandPresenter: CommandPresenter,
    val orderProjections: OrdersProjections,
    val orderPresenter: OrderPresenter,
    val mockPaymentService: MockPaymentService,
) {
    @GetMapping("/commands")
    fun getCommands(): ResponseEntity<*> {
        return ResponseEntity.ok(commandResultStorage.findAll().map { commandPresenter.toFullDto(it) })
    }

    @PostMapping("/mockPayment/{paymentId}")
    fun mockPayment(@PathVariable paymentId: String, @RequestParam amount: BigDecimal): ResponseEntity<*> {
        return either<AppError, OrderProjection> {
            val paymentId = PaymentId(paymentId)
            mockPaymentService.mockPayment(paymentId, amount)
            orderProjections.findByPaymentId(paymentId) ?: raise(OrderNotFoundError.forPaymentId(paymentId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            {
                ResponseEntity.ok(orderPresenter.toFullDto(it))
            })
    }
}