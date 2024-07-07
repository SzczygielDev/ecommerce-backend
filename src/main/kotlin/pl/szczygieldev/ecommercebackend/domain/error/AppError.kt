package pl.szczygieldev.ecommercebackend.domain.error

sealed class AppError(open val message: String)

open abstract class InfrastructureError(message: String) : AppError(message)