package pl.szczygieldev.ecommercebackend.application.port.`in`.command

data class CreateProductCommand(val title: String, val description: String, val price: Double){
    init {
        require(price > 0) { "Product price must be positive value, provided='$price'" }
    }
}