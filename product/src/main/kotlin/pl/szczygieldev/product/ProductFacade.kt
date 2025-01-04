package pl.szczygieldev.product

import java.util.*

interface ProductFacade {
    fun findAll(): List<ProductProjection>
    fun findById(id: UUID): ProductProjection?
}