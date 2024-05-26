package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter.CartPresenter
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.AddItemToCartRequest
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.RemoveItemFromCartRequest
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence.CartProjectionRepository
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence.CartRepository

@RequestMapping("/carts")
@RestController
class CartController(
    val cartUseCase: CartUseCase,
    val cartRepository: CartProjectionRepository,
    val cartPresenter: CartPresenter
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): ResponseEntity<*> {
        return ResponseEntity.ok(cartPresenter.toDto(cartRepository.findById(CartId(id))!!))
    }

    @PostMapping("/{id}/addItem")
    fun addProduct(@PathVariable id: String,@RequestBody request: AddItemToCartRequest) : ResponseEntity<*> {
        cartUseCase.addProductToCart(AddItemToCartCommand(id,request.productId,request.quantity))
        return ResponseEntity.ok(cartPresenter.toDto(cartRepository.findById(CartId(id))!!))
    }

    @PostMapping("/{id}/removeItem")
    fun redddd(@PathVariable id: String,@RequestBody request: RemoveItemFromCartRequest) : ResponseEntity<*> {
        cartUseCase.removeProductFromCart(RemoveItemFromCartCommand(id,request.productId))
        return ResponseEntity.ok(cartPresenter.toDto(cartRepository.findById(CartId(id))!!))
    }

    @PostMapping("/{id}/submit")
    fun submit(@PathVariable id: String) : ResponseEntity<*>{
        cartUseCase.submitCart(SubmitCartCommand(id))
        return ResponseEntity.ok(cartPresenter.toDto(cartRepository.findById(CartId(id))!!))
    }
}