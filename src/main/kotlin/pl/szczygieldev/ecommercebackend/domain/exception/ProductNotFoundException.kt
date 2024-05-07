package pl.szczygieldev.ecommercebackend.domain.exception

import pl.szczygieldev.ecommercebackend.domain.ProductId

class ProductNotFoundException(val productId: ProductId) : RuntimeException()