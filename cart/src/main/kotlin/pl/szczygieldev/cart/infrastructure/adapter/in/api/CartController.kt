package pl.szczygieldev.cart.infrastructure.adapter.`in`.api

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.cart.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.cart.CartProjection
import pl.szczygieldev.cart.application.port.out.CartsProjections
import pl.szczygieldev.cart.domain.AppError
import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.cart.domain.CartNotFoundError
import pl.szczygieldev.cart.domain.UserId
import pl.szczygieldev.cart.infrastructure.adapter.`in`.api.advice.mapToError
import pl.szczygieldev.cart.infrastructure.adapter.`in`.api.resource.AddItemToCartRequest
import pl.szczygieldev.cart.infrastructure.adapter.`in`.api.resource.CartPresenter
import pl.szczygieldev.cart.infrastructure.adapter.`in`.api.resource.SubmitCartRequest
import pl.szczygieldev.ecommercelibrary.command.Mediator
import java.util.UUID
@RequestMapping("/carts")
@RestController("cartModule.CartController")
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
            val cartId = CartId(cart.cartId)
            mediator.send(AddItemToCartCommand(cartId.idAsUUID(), request.productId, request.quantity)).bind()
            cartRepository.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }

    @DeleteMapping("/items/{productId}")
    suspend fun removeItem(@PathVariable productId: UUID): ResponseEntity<*> {
        return either {
            val cart = cartRepository.findActiveForUser(mockUserId) ?: raise(CartNotFoundError.forUserId(mockUserId))
            val cartId = CartId(cart.cartId)
            mediator.send(RemoveItemFromCartCommand(cartId.idAsUUID(), productId)).bind()
            cartRepository.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }

    @PostMapping("/submit")
    suspend fun submit(@RequestBody request: SubmitCartRequest): ResponseEntity<*> {
        return either {
            val cart = cartRepository.findActiveForUser(mockUserId) ?: raise(CartNotFoundError.forUserId(mockUserId))
            val cartId = CartId(cart.cartId)
            mediator.send(SubmitCartCommand(cartId.idAsUUID(), request.deliveryProvider, request.paymentServiceProvider))
                .bind()
            cartRepository.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }
}