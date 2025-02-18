package pl.szczygieldev.cart.infrastructure.adapter.`in`.api.resource

import pl.szczygieldev.cart.domain.CartStatus
import java.math.BigDecimal

internal data class CartDto(val cartId: String, var status: CartStatus, var products: List<CartEntryDto>, val total: BigDecimal)