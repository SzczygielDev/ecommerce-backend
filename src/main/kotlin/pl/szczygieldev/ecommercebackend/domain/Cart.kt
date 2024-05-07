package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.ecommercebackend.domain.event.CartSubmitted
import pl.szczygieldev.ecommercebackend.domain.event.ItemAddedToCart
import pl.szczygieldev.ecommercebackend.domain.event.ItemRemovedFromCart
import pl.szczygieldev.ecommercebackend.domain.exception.CartAlreadySubmittedException
import pl.szczygieldev.ecommercebackend.domain.exception.CartNotActiveException

class Cart private constructor(val cartId: CartId, var status: CartStatus) {
    data class Entry(val productId: ProductId, val quantity: Int)

    companion object {
        fun create(cartId: CartId): Cart {
            return Cart(cartId, CartStatus.ACTIVE)
        }
    }


    private val events = mutableListOf<CartEvent>()

    private var items: MutableList<Entry> = mutableListOf()


    fun addItem(productId: ProductId, quantity: Int) {
        if (status != CartStatus.ACTIVE) {
            throw CartNotActiveException(cartId)
        }

        val entriesForProductId = items.filter {
            it.productId == productId
        }
        if (entriesForProductId.isNotEmpty()) {
            val currentEntry = entriesForProductId.first()
            items[items.indexOf(currentEntry)] = currentEntry.copy(quantity = currentEntry.quantity + quantity)
        } else {
            items.add(Entry(productId, quantity))
        }
        events.add(ItemAddedToCart())
    }

    fun removeItem(productId: ProductId) {
        if (status != CartStatus.ACTIVE) {
            throw CartNotActiveException(cartId)
        }

        items.removeIf { item ->
            item.productId.sameValueAs(productId)
        }
        events.add(ItemRemovedFromCart())
    }

    fun submit() {
        if (status == CartStatus.SUBMITTED) {
            throw CartAlreadySubmittedException(cartId)
        }
        status = CartStatus.SUBMITTED
        events.add(CartSubmitted())
    }
}