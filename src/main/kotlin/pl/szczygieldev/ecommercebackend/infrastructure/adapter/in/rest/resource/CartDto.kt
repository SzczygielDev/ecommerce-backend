package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.ecommercebackend.domain.CartStatus
import java.math.BigDecimal

data class CartDto(val cartId: String, var status: CartStatus, var products: List<CartEntryDto>, val total: BigDecimal)