package pl.szczygieldev.order.infrastructure.adapter.`in`.api

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.order.application.port.`in`.query.model.OrderProjection
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.OrderNotFoundError
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.advice.mapToError
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.presenter.OrderPresenter
import java.util.*


@RestController
@RequestMapping("/admin/orders")
internal class AdminOrderController(
    val ordersProjections: OrdersProjections,
    val orderPresenter: OrderPresenter
) {
    @GetMapping
    fun getOrders(
        @RequestParam(required = false) orderId: UUID?,
        @RequestParam(required = false) cartId: UUID?,
        @RequestParam(required = false) offset: Int?,
        @RequestParam(required = false) limit: Int?,
    ): ResponseEntity<*> {
        if (orderId != null) {
            return getOrder(orderId)
        } else if (cartId != null) {
            return getOrderByCartId(cartId)
        }

        if (offset != null && limit != null) {
            return ResponseEntity.ok(
                ordersProjections.findPage(offset, limit)
                    .map { orderProjection -> orderPresenter.toFullDto(orderProjection) })
        }

        return ResponseEntity.ok(
            ordersProjections.findAll().map { orderProjection -> orderPresenter.toFullDto(orderProjection) })
    }


    private fun getOrder(orderId: UUID): ResponseEntity<*> {
        return either<AppError, OrderProjection> {
            val id = OrderId(orderId)
            ordersProjections.findById(id) ?: raise(OrderNotFoundError.forId(id))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(orderPresenter.toFullDto(it)) })
    }

    private fun getOrderByCartId(cartId: UUID): ResponseEntity<*> {
        return either<AppError, OrderProjection> {
            val id = CartId(cartId)
            ordersProjections.findByCartId(id) ?: raise(OrderNotFoundError.forCartId(id))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(orderPresenter.toFullDto(it)) })
    }
}