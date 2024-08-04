package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.domain.ProductDescription
import pl.szczygieldev.ecommercebackend.domain.ProductId
import pl.szczygieldev.ecommercebackend.domain.ProductPrice
import pl.szczygieldev.ecommercebackend.domain.ProductTitle

data class UpdateProductCommand(
    val productId: ProductId,
    val title: ProductTitle,
    val description: ProductDescription,
    val price: ProductPrice
)