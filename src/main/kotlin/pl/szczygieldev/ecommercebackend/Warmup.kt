package pl.szczygieldev.ecommercebackend

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence.CartRepository

@Component
class Warmup(val cartRepository: CartRepository, val productUseCase: ProductUseCase, val cartEventPublisher : DomainEventPublisher<CartEvent>) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun initData() {
        val cart = Cart.create(cartRepository.nextIdentity())
        val version = cart.version
        cart.occurredEvents().forEach {
            cartEventPublisher.publish(it)
        }
        cartRepository.save(cart,version)


        logger.info("Cart for development created, id='${cart.cartId.id}'")

        productUseCase.create(CreateProductCommand("Produkt A", "Opis opis", 30.0))
        productUseCase.create(CreateProductCommand("Produkt B", "Testowy opis", 72.0))
    }
}