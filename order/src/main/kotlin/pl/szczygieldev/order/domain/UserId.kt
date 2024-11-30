package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.Identity


data class UserId(val id: String): Identity<UserId>(id)
