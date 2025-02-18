package pl.szczygieldev.cart

import java.util.*

interface CartFacade {
    fun findById(id: UUID): CartProjection?
}