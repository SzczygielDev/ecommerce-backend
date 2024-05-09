package pl.szczygieldev.ecommercebackend.application.port.`in`

import pl.szczygieldev.ecommercebackend.application.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.domain.Product

interface ProductUseCase {
    fun create(command: CreateProductCommand) : Product
}