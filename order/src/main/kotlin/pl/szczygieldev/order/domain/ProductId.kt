package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.Identity


internal data class ProductId (val id: String) : Identity<ProductId>(id)