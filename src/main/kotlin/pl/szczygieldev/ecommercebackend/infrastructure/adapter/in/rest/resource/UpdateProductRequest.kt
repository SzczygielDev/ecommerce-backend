package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import java.math.BigDecimal

data class UpdateProductRequest(val title: String, val description: String, val price: BigDecimal)