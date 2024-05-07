package pl.szczygieldev.ecommercebackend.domain.exception

import pl.szczygieldev.ecommercebackend.domain.CartId

class CartAlreadySubmittedException(val cartId: CartId) : RuntimeException()