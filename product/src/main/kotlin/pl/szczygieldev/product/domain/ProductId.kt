package pl.szczygieldev.product.domain

import pl.szczygieldev.shared.ddd.core.Identity

data class ProductId (val id: String) : Identity<ProductId>(id)
