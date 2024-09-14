package pl.szczygieldev.ecommercebackend

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand

@Component
class Warmup(val cartUseCase: CartUseCase, val productUseCase: ProductUseCase) {

    @EventListener(ApplicationReadyEvent::class)
    suspend fun initData() {
        cartUseCase.createCart(CreateCartCommand())
        productUseCase.create(CreateProductCommand("Produkt A", "Opis opis", 30.0))
        productUseCase.create(CreateProductCommand("Produkt B", "Testowy opis", 72.0))
    }
}