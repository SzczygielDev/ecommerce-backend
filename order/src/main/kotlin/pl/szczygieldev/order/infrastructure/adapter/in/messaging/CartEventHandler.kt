package pl.szczygieldev.order.infrastructure.adapter.`in`.messaging

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventHandler
import pl.szczygieldev.order.application.port.`in`.query.model.CartProjection
import pl.szczygieldev.order.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.order.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.order.application.port.out.CartsProjections
import pl.szczygieldev.order.domain.CartStatus
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.CartNotFoundError
import pl.szczygieldev.order.domain.event.*
import java.math.BigDecimal

@Component
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
                    domainEvent.cartId,
                    CartStatus.ACTIVE,
                    BigDecimal.ZERO,
                    emptyList()
                )
            )

            is CartSubmitted -> {
                cartsProjections.save(
                    cartsProjections.findById(domainEvent.cartId)?.copy(status = CartStatus.SUBMITTED)
                        ?: raise(CartNotFoundError.forId(domainEvent.cartId))
                )
                mediator.send(
                    CreateOrderCommand(
                        domainEvent.cartId,
                        domainEvent.paymentServiceProvider,
                        domainEvent.deliveryProvider
                    )
                )
            }

            is ItemAddedToCart -> {
                val cartProjection = cartsProjections.findById(domainEvent.cartId)
                    ?.let {
                        val items = it.items.toMutableList()
                        val entriesForProductId = items.filter {
                            it.productId == domainEvent.productId
                        }
                        if (entriesForProductId.isNotEmpty()) {
                            val currentEntry = entriesForProductId.first()
                            items[items.indexOf(currentEntry)] =
                                currentEntry.copy(quantity = currentEntry.quantity + domainEvent.quantity)
                        } else {
                            items.add(CartProjection.Entry(domainEvent.productId, domainEvent.quantity))
                        }

                        it.copy(items = items)
                    } ?: raise(CartNotFoundError.forId(domainEvent.cartId))
                cartsProjections.save(cartProjection)

                mediator.send(CalculateCartTotalCommand(domainEvent.cartId)).bind()
            }

            is ItemRemovedFromCart -> {
                val cartProjection = cartsProjections.findById(domainEvent.cartId)?.let { cartsProjection ->
                    val itemsToRemove = cartsProjection.items.filter { it.productId.sameValueAs(domainEvent.productId) }
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