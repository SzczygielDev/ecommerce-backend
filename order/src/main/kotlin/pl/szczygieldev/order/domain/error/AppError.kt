package pl.szczygieldev.order.domain.error

sealed class AppError(open val message: String)
abstract class InfrastructureError(message: String) : AppError(message)