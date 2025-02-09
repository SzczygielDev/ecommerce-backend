package pl.szczygieldev.order.domain

internal enum class OrderStatus {
    CREATED,
    ACCEPTED,
    REJECTED,
    IN_PROGRESS,
    READY,
    SENT,
    CANCELLED
}