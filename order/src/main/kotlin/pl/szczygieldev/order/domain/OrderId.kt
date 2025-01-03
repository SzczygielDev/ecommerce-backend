package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.Identity


internal data class OrderId (val id: String) : Identity<OrderId>(id)