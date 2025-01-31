package pl.szczygieldev.order.domain

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercelibrary.ddd.core.EventSourcedEntity
import pl.szczygieldev.order.domain.error.CartAlreadySubmittedError
import pl.szczygieldev.order.domain.error.CartError
import pl.szczygieldev.order.domain.error.CartNotActiveError
import pl.szczygieldev.order.domain.event.*


internal class Cart private constructor(val cartId: CartId) : EventSourcedEntity<CartEvent>() {
    data class Entry(val productId: ProductId, val quantity: Int)

    private var status: CartStatus = CartStatus.ACTIVE

    private var _items: MutableList<Entry> = mutableListOf()
    val items: List<Entry>
        get() = _items.map { it.copy() }.toList()

    companion object {
        fun create(cartId: CartId): Cart {
            val cart = Cart(cartId)
            cart.raiseEvent(CartCreated(cartId))
            return cart
        }

        fun fromEvents(cartId: CartId, events: List<CartEvent>): Cart {
            val cart = Cart(cartId)
            cart.applyAll(events)
            cart.clearOccurredEvents()
            return cart
        }
    }

    fun addItem(productId: ProductId, quantity: Int): Either<CartError, Unit> = either {
        if (status != CartStatus.ACTIVE) {
            raise(CartNotActiveError.forId(cartId))
        }
        require(quantity > 0)  { "Item quantity must be positive value, provided='$quantity'" }
        raiseEvent(ItemAddedToCart(productId, quantity, cartId))
    }

    fun removeItem(productId: ProductId): Either<CartError, Unit> = either {
        if (status != CartStatus.ACTIVE) {
            raise(CartNotActiveError.forId(cartId))
        }
        raiseEvent(ItemRemovedFromCart(productId, cartId))
    }

    fun submit(deliveryProvider: DeliveryProvider, paymentServiceProvider: PaymentServiceProvider): Either<CartError, Unit> = either {
        if (status == CartStatus.SUBMITTED) {
            raise(CartAlreadySubmittedError.forId(cartId))
        }
        raiseEvent(CartSubmitted(cartId,paymentServiceProvider,deliveryProvider))
    }

    //region Event sourcing handlers
    override fun applyEvent(event: CartEvent) {
        when (event) {
            is CartCreated -> {}
            is CartSubmitted -> apply(event)
            is ItemAddedToCart -> apply(event)
            is ItemRemovedFromCart -> apply(event)
        }
    }

    private fun apply(event: CartSubmitted) {
        status = CartStatus.SUBMITTED
    }

    private fun apply(event: ItemAddedToCart) {
        val entriesForProductId = _items.filter {
            it.productId == event.productId
        }
        if (entriesForProductId.isNotEmpty()) {
            val currentEntry = entriesForProductId.first()
            _items[_items.indexOf(currentEntry)] =
                currentEntry.copy(quantity = currentEntry.quantity + event.quantity)
        } else {
            _items.add(Entry(event.productId, event.quantity))
        }
    }

    private fun apply(event: ItemRemovedFromCart) {
        _items.removeIf { item ->
            item.productId.sameValueAs(event.productId)
        }
    }
    //endregion
}