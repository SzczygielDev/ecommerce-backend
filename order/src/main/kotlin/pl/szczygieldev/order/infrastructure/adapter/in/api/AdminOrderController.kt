package pl.szczygieldev.order.infrastructure.adapter.`in`.api

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.OrderId
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
        val id = OrderId(orderId)
        val order = ordersProjections.findById(id) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Cannot find order with id='${id.id()}'.")
            )

        return ResponseEntity.ok(orderPresenter.toFullDto(order))
    }

    private fun getOrderByCartId(cartId: UUID): ResponseEntity<*> {
        val id = CartId(cartId)
        val order = ordersProjections.findByCartId(id) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Cannot find order with cart id='${id.id()}'.")
            )

        return ResponseEntity.ok(orderPresenter.toFullDto(order))
    }
}