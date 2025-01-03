package pl.szczygieldev.product.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.Identity

internal data class ProductId (val id: String) : Identity<ProductId>(id)
