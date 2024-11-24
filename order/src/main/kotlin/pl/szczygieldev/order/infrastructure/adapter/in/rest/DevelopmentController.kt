package pl.szczygieldev.order.infrastructure.adapter.`in`.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.order.infrastructure.adapter.out.command.CommandResultStorage
import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.presenter.CommandPresenter
import pl.szczygieldev.order.infrastructure.adapter.out.integration.payments.MockPaymentService
import java.math.BigDecimal

@RequestMapping("/development")
@RestController
class DevelopmentController(
    val commandResultStorage: CommandResultStorage,
    val commandPresenter: CommandPresenter,
) {
    @GetMapping("/commands")
    fun getCommands(): ResponseEntity<*> {
        return ResponseEntity.ok(commandResultStorage.findAll().map { commandPresenter.toFullDto(it) })
    }
}