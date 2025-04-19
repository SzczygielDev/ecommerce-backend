package pl.szczygieldev.cart

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.cart.application.port.`in`.CartUseCase
import pl.szczygieldev.cart.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.cart.domain.ClientId
import java.util.*


@Component("cartModule.Warmup")
internal class Warmup(val cartUseCase: CartUseCase) {
    @EventListener(ApplicationReadyEvent::class)
    suspend fun initData() {
        cartUseCase.createCart(CreateCartCommand(mockClientId))
    }
}

//TODO - replace when implementing users
internal val mockClientId = ClientId(UUID.fromString("1b18db9a-5c4c-43e8-9cc0-0f7ca3d54b45"))