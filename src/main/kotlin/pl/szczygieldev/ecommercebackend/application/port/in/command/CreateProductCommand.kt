package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.application.handlers.common.Command

data class CreateProductCommand(val title: String, val description: String, val price: Double) : Command(){
    init {
        require(price > 0) { "Product price must be positive value, provided='$price'" }
    }
}