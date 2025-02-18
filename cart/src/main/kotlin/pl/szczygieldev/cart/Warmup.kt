package pl.szczygieldev.cart

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.cart.application.port.`in`.CartUseCase
import pl.szczygieldev.cart.application.port.`in`.command.CreateCartCommand


@Component("cartModule.Warmup")
internal class Warmup(val cartUseCase: CartUseCase) {
    @EventListener(ApplicationReadyEvent::class)
    suspend fun initData() {
        cartUseCase.createCart(CreateCartCommand())
    }
}