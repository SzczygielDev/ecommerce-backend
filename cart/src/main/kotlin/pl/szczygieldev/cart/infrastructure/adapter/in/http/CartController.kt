package pl.szczygieldev.cart.infrastructure.adapter.`in`.http

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.cart.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.AddItemToCartCommand.ProductNotFoundError
import pl.szczygieldev.cart.application.port.out.CartsProjections
import pl.szczygieldev.cart.domain.ProductId
import pl.szczygieldev.cart.infrastructure.adapter.`in`.http.resource.AddItemToCartRequest
import pl.szczygieldev.cart.infrastructure.adapter.`in`.http.resource.CartPresenter
import pl.szczygieldev.cart.infrastructure.adapter.`in`.http.resource.SubmitCartRequest
import pl.szczygieldev.cart.mockClientId
import pl.szczygieldev.ecommercelibrary.command.Mediator
import java.util.UUID

@RequestMapping("/carts")
@RestController("cartModule.CartController")
internal class CartController(
    val mediator: Mediator,
    val cartRepository: CartsProjections,
    val cartPresenter: CartPresenter
) {
    @GetMapping
    fun get(): ResponseEntity<*> {
        val cart = cartRepository.findActiveForClient(mockClientId)
            ?: return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "No cart found"))

        return ResponseEntity.ok(cartPresenter.toDto(cart))
    }

    @PostMapping("/items")
    suspend fun addItem(@RequestBody request: AddItemToCartRequest): ResponseEntity<*> {
        val result = mediator.send(AddItemToCartCommand(mockClientId, ProductId(request.productId), request.quantity))

        result.onLeft { error ->
            when (error) {
                is AddItemToCartCommand.CartNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))

                is ProductNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))

                is AddItemToCartCommand.CartNotActiveError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))
            }
        }

        val foundCart =
            cartRepository.findActiveForClient(mockClientId) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Cart not found"))

        return ResponseEntity.ok(cartPresenter.toDto(foundCart))
    }

    @DeleteMapping("/items/{productId}")
    suspend fun removeItem(@PathVariable productId: UUID): ResponseEntity<*> {
        val result = mediator.send(RemoveItemFromCartCommand(mockClientId, productId))

        result.onLeft { error ->
            when (error) {
                is RemoveItemFromCartCommand.CartNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))

                is RemoveItemFromCartCommand.CartNotActiveError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))
            }
        }

        val foundCart =
            cartRepository.findActiveForClient(mockClientId) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Cart not found"))

        return ResponseEntity.ok(cartPresenter.toDto(foundCart))
    }

    @PostMapping("/submit")
    suspend fun submit(@RequestBody request: SubmitCartRequest): ResponseEntity<*> {
        val result = mediator.send(
            SubmitCartCommand(
                mockClientId,
                request.deliveryProvider,
                request.paymentServiceProvider
            )
        )

        result.onLeft { error ->
            when (error) {
                is SubmitCartCommand.CartNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))
                is SubmitCartCommand.CartAlreadySubmittedError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))
            }
        }

        val foundCart =
            cartRepository.findActiveForClient(mockClientId) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Cart not found"))


        return ResponseEntity.ok(cartPresenter.toDto(foundCart))
    }
}