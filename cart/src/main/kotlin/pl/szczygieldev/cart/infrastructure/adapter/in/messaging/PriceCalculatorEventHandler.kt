package pl.szczygieldev.cart.infrastructure.adapter.`in`.messaging

import com.trendyol.kediatr.NotificationHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import pl.szczygieldev.cart.application.port.out.CartsProjections
import pl.szczygieldev.cart.domain.CartTotalRecalculated
import pl.szczygieldev.cart.domain.PriceCalculatorEvent
@Component("cartModule.PriceCalculatorEventHandler")
internal class PriceCalculatorEventHandler(private val cartsProjections: CartsProjections) :
    NotificationHandler<PriceCalculatorEvent> {
    companion object {
        private val log = KotlinLogging.logger { }
    }


    override suspend fun handle(notification: PriceCalculatorEvent){
        val domainEvent = notification

        try {

            when (domainEvent) {
                is CartTotalRecalculated -> {
                    val foundCart =
                        cartsProjections.findById(domainEvent.cartId) ?: throw Exception("Cart not found for id='${domainEvent.cartId.id()}'")

                    cartsProjections.save(foundCart.copy(amount = domainEvent.amount))
                }
            }
            log.info { "Event handled=${domainEvent}" }
        }
        catch (ex: Exception){
            log.error { "Event handling failed=${domainEvent}" }
        }
    }
}