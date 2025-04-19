package pl.szczygieldev.cart.domain


internal sealed class SubmitError(message: String) {
    internal data class CartAlreadySubmittedError(val message: String) : SubmitError(message) {
        companion object {
            fun forId(id: CartId): CartAlreadySubmittedError {
                return CartAlreadySubmittedError("Cart with id='${id.id()}' is already submitted.")
            }
        }
    }
}


internal sealed class AddToCartError(open val message: String) {
    internal data class CartNotActiveError(override val message: String) : AddToCartError(message) {
        companion object {
            fun forId(id: CartId): CartNotActiveError {
                return CartNotActiveError("Cart with id='${id.id()}' is not active.")
            }
        }
    }
}


internal sealed class ItemRemoveError(open val message: String) {
    internal data class CartNotActiveError(override val message: String) : ItemRemoveError(message) {
        companion object {
            fun forId(id: CartId): CartNotActiveError {
                return CartNotActiveError("Cart with id='${id.id()}' is not active.")
            }
        }
    }

}
