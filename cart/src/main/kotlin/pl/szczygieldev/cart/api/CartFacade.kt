package pl.szczygieldev.cart.api

import java.util.*

interface CartFacade {
    fun findById(id: UUID): CartProjection?
}