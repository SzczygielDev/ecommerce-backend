package pl.szczygieldev.cart.infrastructure.adapter.`in`.messaging

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.cart.CartProjection
import pl.szczygieldev.cart.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.cart.application.port.out.CartsProjections
import pl.szczygieldev.cart.domain.*
import pl.szczygieldev.cart.domain.CartCreated
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.cart.domain.CartNotFoundError
import pl.szczygieldev.cart.domain.CartStatus
import pl.szczygieldev.cart.domain.CartSubmitted
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventHandler

import java.math.BigDecimal

@Component("cartModule.CartEventHandler")
internal class CartEventHandler(
    private val cartsProjections: CartsProjections,
    private val mediator: Mediator
) : DomainEventHandler<CartEvent> {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @EventListener
    override suspend fun handleEvent(domainEvent: CartEvent) = either<CommandError, Unit> {
        when (domainEvent) {
            is CartCreated -> cartsProjections.save(
                CartProjection(
                    domainEvent.cartId.id,
                    CartStatus.ACTIVE.toString(),
                    BigDecimal.ZERO,
                    emptyList()
                )
            )

            is CartSubmitted -> {
                cartsProjections.save(
                    cartsProjections.findById(domainEvent.cartId)?.copy(status = CartStatus.SUBMITTED.toString())
                        ?: raise(CartNotFoundError.forId(domainEvent.cartId))
                )
            }

            is ItemAddedToCart -> {
                val cartProjection = cartsProjections.findById(domainEvent.cartId)
                    ?.let {
                        val items = it.items.toMutableList()
                        val entriesForProductId = items.filter {
                            it.productId == domainEvent.productId.id
                        }
                        if (entriesForProductId.isNotEmpty()) {
                            val currentEntry = entriesForProductId.first()
                            items[items.indexOf(currentEntry)] =
                                currentEntry.copy(quantity = currentEntry.quantity + domainEvent.quantity)
                        } else {
                            items.add(CartProjection.Entry(domainEvent.productId.id, domainEvent.quantity))
                        }

                        it.copy(items = items)
                    } ?: raise(CartNotFoundError.forId(domainEvent.cartId))
                cartsProjections.save(cartProjection)

                mediator.send(CalculateCartTotalCommand(domainEvent.cartId)).bind()
            }

            is ItemRemovedFromCart -> {
                val cartProjection = cartsProjections.findById(domainEvent.cartId)?.let { cartsProjection ->
                    val itemsToRemove = cartsProjection.items.filter { it.productId == domainEvent.productId.id }
                    val items = cartsProjection.items.toMutableList()
                    items.removeAll(itemsToRemove)

                    cartsProjection.copy(items = items)
                } ?: raise(CartNotFoundError.forId(domainEvent.cartId))
                cartsProjections.save(cartProjection)

                mediator.send(CalculateCartTotalCommand(domainEvent.cartId)).bind()
            }


        }
    }.fold({
        log.error { "Event handling failed=${domainEvent}" }
    }, {
        log.info { "Event handled=${domainEvent}" }
    })
}