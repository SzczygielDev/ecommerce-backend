package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ddd.core.DomainEventHandler
import pl.szczygieldev.ecommercebackend.application.model.CartProjection
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.domain.CartStatus
import pl.szczygieldev.ecommercebackend.domain.event.*
import pl.szczygieldev.ecommercebackend.domain.exception.CartNotFoundException
import java.lang.Exception
import java.math.BigDecimal

@Component
class CartEventHandler(private val cartsProjections: CartsProjections) : DomainEventHandler<CartEvent> {
    @EventListener
    override fun handleEvent(domainEvent: CartEvent) {
        when (domainEvent) {
            is CartCreated -> cartsProjections.save(
                CartProjection(
                    domainEvent.cartId,
                    CartStatus.ACTIVE,
                    BigDecimal.ZERO,
                    emptyList()
                )
            )
            is CartSubmitted -> cartsProjections.save(
                cartsProjections.findById(domainEvent.cartId)?.copy(status = CartStatus.SUBMITTED) ?: throw CartNotFoundException(domainEvent.cartId)
            )
            is ItemAddedToCart -> cartsProjections.save(cartsProjections.findById(domainEvent.cartId)
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
                } ?: throw CartNotFoundException(domainEvent.cartId))

            is ItemRemovedFromCart ->
                cartsProjections.save(cartsProjections.findById(domainEvent.cartId)?.let { cartsProjection ->
                    val itemsToRemove = cartsProjection.items.filter { it.productId.sameValueAs( domainEvent.productId)  }
                    val items = cartsProjection.items.toMutableList()
                    items.removeAll(itemsToRemove)

                    cartsProjection.copy(items = items)
                } ?: throw CartNotFoundException(domainEvent.cartId))


        }
    }
}