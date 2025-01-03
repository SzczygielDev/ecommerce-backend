package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.order.domain.CartStatus
import java.math.BigDecimal

internal data class CartDto(val cartId: String, var status: CartStatus, var products: List<CartEntryDto>, val total: BigDecimal)