package pl.szczygieldev.ecommercebackend.domain.error

import pl.szczygieldev.ecommercebackend.domain.CartId

sealed interface CartError : AppError

data class CartNotFoundError(val message: String) : CartError {
    companion object {
        fun forId(id: CartId): CartNotFoundError {
            return CartNotFoundError("Cannot find cart with id='${id.id()}'.")
        }
    }
}

data class CartNotActiveError(val message: String) : CartError {
    companion object {
        fun forId(id: CartId): CartNotActiveError {
            return CartNotActiveError("Cart with id='${id.id()}' is not active.")
        }
    }
}

data class CartAlreadySubmittedError(val message: String) : CartError {
    companion object {
        fun forId(id: CartId): CartAlreadySubmittedError {
            return CartAlreadySubmittedError("Cart with id='${id.id()}' is already submitted.")
        }
    }
}