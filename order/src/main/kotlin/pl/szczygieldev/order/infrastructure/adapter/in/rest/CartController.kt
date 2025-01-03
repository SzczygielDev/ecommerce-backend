package pl.szczygieldev.order.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.order.application.port.`in`.query.model.CartProjection
import pl.szczygieldev.order.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.order.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.order.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.order.application.port.out.CartsProjections
import pl.szczygieldev.order.domain.UserId
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.CartNotFoundError
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.presenter.CartPresenter
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource.AddItemToCartRequest
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource.SubmitCartRequest
import java.util.UUID
@RequestMapping("/carts")
@RestController
internal class CartController(
    val mediator: Mediator,
    val cartRepository: CartsProjections,
    val cartPresenter: CartPresenter
) {
    //TODO - replace when implementing users
    private val mockUserId = UserId(UUID.randomUUID().toString())

    @GetMapping
    fun get(): ResponseEntity<*> {
        return either<AppError, CartProjection> {
            cartRepository.findActiveForUser(mockUserId) ?: raise(CartNotFoundError.forUserId(mockUserId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }

    @PostMapping("/items")
    suspend fun addItem(@RequestBody request: AddItemToCartRequest): ResponseEntity<*> {
        return either {
            val cart = cartRepository.findActiveForUser(mockUserId) ?: raise(CartNotFoundError.forUserId(mockUserId))
            val cartId = cart.cartId
            mediator.send(AddItemToCartCommand(cartId.id(), request.productId, request.quantity)).bind()
            cartRepository.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }

    @DeleteMapping("/items/{productId}")
    suspend fun removeItem(@PathVariable productId: String): ResponseEntity<*> {
        return either {
            val cart = cartRepository.findActiveForUser(mockUserId) ?: raise(CartNotFoundError.forUserId(mockUserId))
            val cartId = cart.cartId
            mediator.send(RemoveItemFromCartCommand(cartId.id(), productId)).bind()
            cartRepository.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }

    @PostMapping("/submit")
    suspend fun submit(@RequestBody request: SubmitCartRequest): ResponseEntity<*> {
        return either {
            val cart = cartRepository.findActiveForUser(mockUserId) ?: raise(CartNotFoundError.forUserId(mockUserId))
            val cartId = cart.cartId
            mediator.send(SubmitCartCommand(cartId.id(), request.deliveryProvider, request.paymentServiceProvider))
                .bind()
            cartRepository.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }
}