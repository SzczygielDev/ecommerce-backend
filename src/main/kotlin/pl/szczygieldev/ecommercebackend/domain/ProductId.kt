package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.ecommercebackend.domain.util.Identity

class ProductId private constructor(id: String) : Identity<ProductId>(id) {
    companion object {
        fun valueOf(id: String): ProductId {
            return ProductId(id)
        }
    }


}
