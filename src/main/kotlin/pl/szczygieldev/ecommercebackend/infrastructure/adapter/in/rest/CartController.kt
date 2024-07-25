package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.ecommercebackend.application.model.CartProjection
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.DeliveryProvider
import pl.szczygieldev.ecommercebackend.domain.PaymentServiceProvider
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotFoundError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter.CartPresenter
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.AddItemToCartRequest
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.SubmitCartRequest

@RequestMapping("/carts")
@RestController
class CartController(
    val cartUseCase: CartUseCase,
    val cartRepository: CartsProjections,
    val cartPresenter: CartPresenter
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): ResponseEntity<*> {
        return either<AppError, CartProjection> {
            val cartId = CartId(id)
            cartRepository.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }

    @PostMapping("/{id}/items")
    fun addItem(@PathVariable id: String, @RequestBody request: AddItemToCartRequest): ResponseEntity<*> {
        return either {
            val cartId = CartId(id)
            cartUseCase.addProductToCart(AddItemToCartCommand(id, request.productId, request.quantity)).bind()
            cartRepository.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }

    @DeleteMapping("/{id}/items/{productId}")
    fun removeItem(@PathVariable id: String,@PathVariable productId:String): ResponseEntity<*> {
        return either {
            val cartId = CartId(id)
            cartUseCase.removeProductFromCart(RemoveItemFromCartCommand(id, productId)).bind()
            cartRepository.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }

    @PostMapping("/{id}/submit")
    fun submit(@PathVariable id: String, @RequestBody request: SubmitCartRequest): ResponseEntity<*> {
        return either {
            val cartId = CartId(id)
            cartUseCase.submitCart(SubmitCartCommand(id,request.deliveryProvider,request.paymentServiceProvider)).bind()
            cartRepository.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(cartPresenter.toDto(it)) })
    }
}