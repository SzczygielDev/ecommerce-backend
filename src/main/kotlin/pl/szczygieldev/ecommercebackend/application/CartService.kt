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
private class CartService(val carts: Carts, val products: Products, val cartEventPublisher : DomainEventPublisher<CartEvent>) : CartUseCase {
    override fun submitCart(command: SubmitCartCommand){
        val cartId = CartId.valueOf(command.cartId)
        val cart = carts.findById(cartId)   ?: throw CartNotFoundException(cartId)
        cart.submit()

        cart.occurredEvents().forEach {
            cartEventPublisher.publish(it)
        }
        carts.save(cart)
    }

    override fun addProductToCart(command: AddItemToCartCommand){
        val cartId = CartId.valueOf(command.cartId)
        val cart = carts.findById(cartId) ?: throw CartNotFoundException(cartId)

        val productId = ProductId.valueOf(command.productId)
        val product = products.findById(productId) ?: throw ProductNotFoundException(productId)

        cart.addItem(product.productId,command.quantity)
        cart.occurredEvents().forEach {
            cartEventPublisher.publish(it)
        }
        carts.save(cart)
    }

    override fun removeProductFromCart(command: RemoveItemFromCartCommand){
        val cartId = CartId.valueOf(command.cartId)
        val cart = carts.findById(cartId) ?: throw CartNotFoundException(cartId)
        cart.removeItem(ProductId.valueOf(command.productId))
        cart.occurredEvents().forEach {
            cartEventPublisher.publish(it)
        }
        carts.save(cart)
    }
}