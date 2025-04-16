package pl.szczygieldev.cart.infrastructure.adapter.`in`.messaging

import arrow.core.raise.either
import com.trendyol.kediatr.NotificationHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import pl.szczygieldev.cart.application.port.out.CartsProjections
import pl.szczygieldev.cart.domain.AppError
import pl.szczygieldev.cart.domain.CartNotFoundError
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
        either<AppError, Unit> {
            when (domainEvent) {
                is CartTotalRecalculated -> {
                    val foundCart =
                        cartsProjections.findById(domainEvent.cartId) ?: raise(CartNotFoundError.forId(domainEvent.cartId))

                    cartsProjections.save(foundCart.copy(amount = domainEvent.amount))
                }
            }
        }.fold({
            log.error { "Event handling failed=${domainEvent}" }
        }, {
            log.info { "Event handled=${domainEvent}" }
        })
    }
}