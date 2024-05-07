package pl.szczygieldev.ecommercebackend.domain.exception

import pl.szczygieldev.ecommercebackend.domain.CartId

class CartNotFoundException(val cartId: CartId) : RuntimeException()