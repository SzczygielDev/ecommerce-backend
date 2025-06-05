package pl.szczygieldev.cart.infrastructure.adapter.`in`.messaging

import arrow.core.raise.either
import com.trendyol.kediatr.NotificationHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import pl.szczygieldev.cart.api.CartProjection
import pl.szczygieldev.cart.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.cart.application.port.out.CartsProjections
import pl.szczygieldev.cart.domain.*
import pl.szczygieldev.cart.domain.CartCreated
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.cart.domain.CartStatus
import pl.szczygieldev.cart.domain.CartSubmitted
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.ecommercelibrary.command.Mediator

import java.math.BigDecimal

@Component("cartModule.CartEventHandler")
internal class CartEventHandler(
    private val cartsProjections: CartsProjections,
    private val mediator: Mediator
) : NotificationHandler<CartEvent> {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    override suspend fun handle(notification: CartEvent) {
        val domainEvent = notification

        try {
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
                            ?:  throw Exception("Cart not found for id='${domainEvent.cartId.id()}'")
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
                        } ?:  throw Exception("Cart not found for id='${domainEvent.cartId.id()}'")
                    cartsProjections.save(cartProjection)

                    val result = mediator.send(CalculateCartTotalCommand(domainEvent.cartId))
                    result.onLeft { error ->
                        throw Exception("Failed to calculate cart total for cart id='${domainEvent.cartId.id()}'")
                    }
                }

                is ItemRemovedFromCart -> {
                    val cartProjection = cartsProjections.findById(domainEvent.cartId)?.let { cartsProjection ->
                        val itemsToRemove = cartsProjection.items.filter { it.productId == domainEvent.productId.id }
                        val items = cartsProjection.items.toMutableList()
                        items.removeAll(itemsToRemove)

                        cartsProjection.copy(items = items)
                    } ?:  throw Exception("Cart not found for id='${domainEvent.cartId.id()}'")
                    cartsProjections.save(cartProjection)

                    val result = mediator.send(CalculateCartTotalCommand(domainEvent.cartId))

                    result.onLeft { error ->
                        throw Exception("Failed to calculate cart total for cart id='${domainEvent.cartId.id()}'")
                    }
                }


            }
            log.info { "Event handled=${domainEvent}" }
        }
        catch (e: Exception) {
            log.error { "Event handling failed=${domainEvent}" }
        }
    }
}