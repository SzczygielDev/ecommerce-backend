package pl.szczygieldev.ecommercebackend.application

import pl.szczygieldev.ecommercebackend.application.architecture.UseCase
import pl.szczygieldev.ecommercebackend.application.command.AddItemToCartCommand
import pl.szczygieldev.ecommercebackend.application.command.RemoveItemFromCartCommand
import pl.szczygieldev.ecommercebackend.application.command.SubmitCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.out.Carts
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.ProductId
import pl.szczygieldev.ecommercebackend.domain.exception.CartNotFoundException
import pl.szczygieldev.ecommercebackend.domain.exception.ProductNotFoundException

@UseCase
private class CartService(val carts: Carts, val products: Products) : CartUseCase {
    override fun submitCart(command: SubmitCartCommand){
        val cartId = CartId.valueOf(command.cartId)
        val cart = carts.findById(cartId)   ?: throw CartNotFoundException(cartId)
        cart.submit()
    }

    override fun addProductToCart(command: AddItemToCartCommand){
        val cartId = CartId.valueOf(command.cartId)
        val cart = carts.findById(cartId) ?: throw CartNotFoundException(cartId)

        val productId = ProductId.valueOf(command.productId)
        val product = products.findById(productId) ?: throw ProductNotFoundException(productId)

        cart.addItem(product.productId,command.quantity)
    }

    override fun removeProductFromCart(command: RemoveItemFromCartCommand){
        val cartId = CartId.valueOf(command.cartId)
        val cart = carts.findById(cartId) ?: throw CartNotFoundException(cartId)
        cart.removeItem(ProductId.valueOf(command.productId))
    }
}