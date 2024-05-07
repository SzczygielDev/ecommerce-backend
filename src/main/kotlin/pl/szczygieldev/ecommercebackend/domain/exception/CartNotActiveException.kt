package pl.szczygieldev.ecommercebackend.domain.exception

import pl.szczygieldev.ecommercebackend.domain.CartId

class CartNotActiveException(val cartId: CartId) : RuntimeException()