package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercebackend.application.port.`in`.query.model.OrderProjection
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.command.CommandResultStorage
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter.OrderPresenter
import java.util.*


@RestController
@RequestMapping("/admin/orders")
class AdminOrderController(
    val ordersProjections: OrdersProjections,
    val commandResultStorage: CommandResultStorage,
    val orderPresenter: OrderPresenter
) {
    @GetMapping
    fun getOrders(
        @RequestParam(required = false) orderId: UUID?,
        @RequestParam(required = false) cartId: UUID?
    ): ResponseEntity<*> {
        if (orderId != null) {
            return getOrder(orderId)
        } else if (cartId != null) {
            return getOrderByCartId(cartId)
        }

        return ResponseEntity.ok(
            ordersProjections.findAll().map { orderProjection -> orderPresenter.toFullDto(orderProjection) })
    }


    private fun getOrder(orderId: UUID): ResponseEntity<*> {
        return either<AppError, OrderProjection> {
            val id = OrderId(orderId.toString())
            ordersProjections.findById(id) ?: raise(OrderNotFoundError.forId(id))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(orderPresenter.toFullDto(it)) })
    }

    private fun getOrderByCartId(cartId: UUID): ResponseEntity<*> {
        return either<AppError, OrderProjection> {
            val id = CartId(cartId.toString())
            ordersProjections.findByCartId(id) ?: raise(OrderNotFoundError.forCartId(id))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(orderPresenter.toFullDto(it)) })
    }
}