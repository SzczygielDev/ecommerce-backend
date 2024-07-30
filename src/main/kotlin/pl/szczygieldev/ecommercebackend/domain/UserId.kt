package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.shared.ddd.core.Identity

data class UserId(val id: String): Identity<UserId>(id)
