package pl.szczygieldev.ecommercebackend.domain

enum class OrderStatus {
    CREATED,
    ACCEPTED,
    REJECTED,
    IN_PROGRESS,
    READY,
    SENT,
    CANCELLED
}