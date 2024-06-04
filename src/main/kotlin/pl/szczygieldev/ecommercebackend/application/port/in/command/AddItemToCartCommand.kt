package pl.szczygieldev.ecommercebackend.application.port.`in`.command


data class AddItemToCartCommand(val cartId: String, val productId: String, val quantity: Int){
    init {
        require(quantity > 0) { "Item quantity must be positive value, provided='$quantity'" }
    }
}