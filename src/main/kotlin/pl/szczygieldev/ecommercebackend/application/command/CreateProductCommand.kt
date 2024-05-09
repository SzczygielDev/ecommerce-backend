package pl.szczygieldev.ecommercebackend.application.command

data class CreateProductCommand(val title: String, val description: String, val price: Double)