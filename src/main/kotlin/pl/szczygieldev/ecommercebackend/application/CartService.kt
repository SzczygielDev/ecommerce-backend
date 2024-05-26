package pl.szczygieldev.ecommercebackend.application

import pl.szczygieldev.ddd.core.DomainEventPublisher
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.out.Carts
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.ProductId
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.ecommercebackend.domain.exception.CartNotFoundException
import pl.szczygieldev.ecommercebackend.domain.exception.ProductNotFoundException
import pl.szczygieldev.shared.architecture.UseCase

@UseCase
private class CartService(
    val carts: Carts,
    val products: Products,
    val cartEventPublisher: DomainEventPublisher<CartEvent>,

) : CartUseCase {
    override fun submitCart(command: SubmitCartCommand) {
        val cartId = CartId(command.cartId)
        val cart = carts.findById(cartId) ?: throw CartNotFoundException(cartId)
        val currentVersion = cart.version;
        cart.submit()

        cart.occurredEvents().forEach {
            cartEventPublisher.publish(it)
        }
        carts.save(cart,currentVersion)
    }

    override fun addProductToCart(command: AddItemToCartCommand) {
        val cartId = CartId(command.cartId)
        val cart = carts.findById(cartId) ?: throw CartNotFoundException(cartId)
        val currentVersion = cart.version;

        val productId = ProductId(command.productId)
        val product = products.findById(productId) ?: throw ProductNotFoundException(productId)


        cart.addItem(product.productId, command.quantity)
        cart.occurredEvents().forEach {
            cartEventPublisher.publish(it)
        }
        carts.save(cart, currentVersion)
    }

    override fun removeProductFromCart(command: RemoveItemFromCartCommand) {
        val cartId = CartId(command.cartId)
        val cart = carts.findById(cartId) ?: throw CartNotFoundException(cartId)
        val currentVersion = cart.version;

        cart.removeItem(ProductId(command.productId))
        cart.occurredEvents().forEach {
            cartEventPublisher.publish(it)
        }
        carts.save(cart,currentVersion)
    }
}