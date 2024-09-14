package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.shared.ddd.core.Identity

data class OrderId (val id: String) : Identity<OrderId>(id)