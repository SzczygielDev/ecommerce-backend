package pl.szczygieldev.product

interface ProductFacade {
    fun findAll(): List<ProductProjection>
    fun findById(id: String): ProductProjection?
}