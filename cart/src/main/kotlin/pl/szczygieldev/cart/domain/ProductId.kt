package pl.szczygieldev.cart.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.UuidIdentity
import java.util.UUID


internal data class ProductId (val id: UUID) : UuidIdentity<ProductId>(id)