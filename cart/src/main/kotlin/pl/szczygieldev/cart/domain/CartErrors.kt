package pl.szczygieldev.cart.domain



internal sealed class CartError(message: String) : AppError(message)

internal data class CartNotFoundError(override val message: String) : CartError(message) {
    companion object {
        fun forId(id: CartId): CartNotFoundError {
            return CartNotFoundError("Cannot find cart with id='${id.id()}'.")
        }

        fun forUserId(id: UserId): CartNotFoundError {
            return CartNotFoundError("Cannot find cart for user with id='${id.id()}'.")
        }
    }
}

internal data class CartNotActiveError(override val message: String) :  CartError(message) {
    companion object {
        fun forId(id: CartId): CartNotActiveError {
            return CartNotActiveError("Cart with id='${id.id()}' is not active.")
        }
    }
}

internal data class CartAlreadySubmittedError(override val message: String) :  CartError(message) {
    companion object {
        fun forId(id: CartId): CartAlreadySubmittedError {
            return CartAlreadySubmittedError("Cart with id='${id.id()}' is already submitted.")
        }
    }
}