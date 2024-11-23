package pl.szczygieldev.order.domain

enum class OrderStatus {
    CREATED,
    ACCEPTED,
    REJECTED,
    IN_PROGRESS,
    READY,
    SENT,
    CANCELLED
}