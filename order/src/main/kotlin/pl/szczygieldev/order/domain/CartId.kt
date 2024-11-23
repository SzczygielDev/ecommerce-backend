package pl.szczygieldev.order.domain

import pl.szczygieldev.shared.ddd.core.Identity

data class CartId (val id:String): Identity<CartId>(id)