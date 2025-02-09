package pl.szczygieldev.external.psp

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.external.psp.dto.RegisterPaymentRequest
import pl.szczygieldev.external.psp.dto.RegisterPaymentResponse
import pl.szczygieldev.external.psp.dto.VerifyPaymentRequest
import java.math.BigDecimal
import java.net.URL
import java.util.UUID

@RestController
@RequestMapping(("/external/psp/"))
internal class PspController(val pspService: PspService) {
    @PostMapping("/register")
    fun register(@RequestBody registerPaymentRequest: RegisterPaymentRequest): ResponseEntity<*> {
        val registeredPayment = pspService.register(
            registerPaymentRequest.amount,
            URL(registerPaymentRequest.returnUrl)
        )
        return ResponseEntity.ok(
            RegisterPaymentResponse(registeredPayment.id, registeredPayment.url.toString())
        )
    }

    @PostMapping("/verify")
    fun verify(@RequestBody verifyPaymentRequest: VerifyPaymentRequest): ResponseEntity<*> {
        pspService.verify(UUID.fromString(verifyPaymentRequest.paymentId))
        return ResponseEntity.ok("OK")
    }

    @GetMapping("/{paymentId}")
    fun get(@PathVariable paymentId: UUID): ResponseEntity<*> {
        return ResponseEntity.ok(pspService.get(paymentId))
    }

    @PostMapping("/pay/{paymentId}")
    fun pay(@PathVariable paymentId: UUID, @RequestParam amount: BigDecimal): ResponseEntity<*> {
        pspService.pay(paymentId, amount)
        return ResponseEntity.ok(pspService.get(paymentId))
    }
}