package pl.szczygieldev.ecommercebackend.domain.error

import pl.szczygieldev.ecommercebackend.domain.CartId

sealed class CartError(message: String) : AppError(message)

data class CartNotFoundError(override val message: String) : CartError(message) {
    companion object {
        fun forId(id: CartId): CartNotFoundError {
            return CartNotFoundError("Cannot find cart with id='${id.id()}'.")
        }
    }
}

data class CartNotActiveError(override val message: String) :  CartError(message) {
    companion object {
        fun forId(id: CartId): CartNotActiveError {
            return CartNotActiveError("Cart with id='${id.id()}' is not active.")
        }
    }
}

data class CartAlreadySubmittedError(override val message: String) :  CartError(message) {
    companion object {
        fun forId(id: CartId): CartAlreadySubmittedError {
            return CartAlreadySubmittedError("Cart with id='${id.id()}' is already submitted.")
        }
    }
}