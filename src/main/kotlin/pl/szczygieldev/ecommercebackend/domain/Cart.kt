package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.ddd.core.AggregateRoot
import pl.szczygieldev.ecommercebackend.domain.event.*
import pl.szczygieldev.ecommercebackend.domain.exception.CartAlreadySubmittedException
import pl.szczygieldev.ecommercebackend.domain.exception.CartNotActiveException

class Cart private constructor(val cartId: CartId) : AggregateRoot<CartEvent>() {
    private var status: CartStatus = CartStatus.ACTIVE

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

    //region Event sourcing handlers
    override fun applyEvent(event: CartEvent) {
        when (event) {
            is CartCreated -> {}
            is CartSubmitted -> apply(event)
            is ItemAddedToCart -> apply(event)
            is ItemRemovedFromCart -> apply(event)
        }
    }

    private fun apply(event:CartSubmitted){
        status = CartStatus.SUBMITTED
    }

    private fun apply(event:ItemAddedToCart){
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

    private fun apply(event:ItemRemovedFromCart){
        items.removeIf { item ->
            item.productId.sameValueAs(event.productId)
        }
    }
    //endregion
}