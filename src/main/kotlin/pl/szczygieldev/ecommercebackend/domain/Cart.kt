package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.ecommercebackend.domain.event.*
import pl.szczygieldev.ecommercebackend.domain.exception.CartAlreadySubmittedException
import pl.szczygieldev.ecommercebackend.domain.exception.CartNotActiveException

class Cart private constructor(val cartId: CartId) {
    private var status: CartStatus = CartStatus.ACTIVE;

    data class Entry(val productId: ProductId, val quantity: Int)

    companion object {
        fun create(cartId: CartId): Cart {
            val cart = Cart(cartId)
            cart.raiseEvent(CartCreated(cartId))
            return cart
        }

        fun fromEvents(cartId: CartId, events: List<CartEvent>): Cart {
            val cart = Cart(cartId)
            cart.applyAll(events)
            return cart
        }
    }
    private val events = mutableListOf<CartEvent>()
    fun occurredEvents(): List<CartEvent> = events.toList()
    fun clearOccurredEvents() = events.clear()

    private var items: MutableList<Entry> = mutableListOf()

    fun addItem(productId: ProductId, quantity: Int) {
        if (status != CartStatus.ACTIVE) {
            throw CartNotActiveException(cartId)
        }
        raiseEvent(ItemAddedToCart(productId, quantity, cartId))
    }

    fun removeItem(productId: ProductId) {
        if (status != CartStatus.ACTIVE) {
            throw CartNotActiveException(cartId)
        }
        raiseEvent(ItemRemovedFromCart(productId, cartId))
    }

    fun submit() {
        if (status == CartStatus.SUBMITTED) {
            throw CartAlreadySubmittedException(cartId)
        }
        raiseEvent(CartSubmitted(cartId))
    }

    private fun raiseEvent(event: CartEvent) {
        events.add(event)
        apply(event)
    }
    private fun apply(event: CartEvent) {
        when (event) {
            is CartCreated -> {}
            is CartSubmitted -> status = CartStatus.SUBMITTED
            is ItemAddedToCart -> {
                val entriesForProductId = items.filter {
                    it.productId == event.productId
                }
                if (entriesForProductId.isNotEmpty()) {
                    val currentEntry = entriesForProductId.first()
                    items[items.indexOf(currentEntry)] =
                        currentEntry.copy(quantity = currentEntry.quantity + event.quantity)
                } else {
                    items.add(Entry(event.productId, event.quantity))
                }
            }

            is ItemRemovedFromCart -> items.removeIf { item ->
                item.productId.sameValueAs(event.productId)
            }

        }

    }

    private fun applyAll(events: List<CartEvent>) {
        events.forEach { event ->
            apply(event)
        }
    }
}