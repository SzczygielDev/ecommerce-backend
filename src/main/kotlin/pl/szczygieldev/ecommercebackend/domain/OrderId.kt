package pl.szczygieldev.ecommercebackend.domain

class OrderId private constructor(val id: String) {
    companion object {
        fun valueOf(id: String): OrderId {
            return OrderId(id)
        }
    }
}
