package pl.szczygieldev.cart.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.Identity

internal data class UserId(val id: String): Identity<UserId>(id)