package pl.szczygieldev.cart.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.cart.application.port.`in`.CartUseCase
import pl.szczygieldev.cart.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.cart.application.port.out.Carts
import pl.szczygieldev.cart.application.port.out.Products
import pl.szczygieldev.cart.domain.*
import pl.szczygieldev.cart.domain.AppError
import pl.szczygieldev.cart.domain.Cart
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.cart.domain.CartNotFoundError
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher


@UseCase
internal class CartService(
    val carts: Carts,
    val products: Products,
    val cartEventPublisher: DomainEventPublisher<CartEvent>,
) : CartUseCase {

    override suspend fun createCart(command: CreateCartCommand): Either<AppError, Unit> = either {
        val cart = Cart.create(carts.nextIdentity())
        val version = cart.version
        val events = cart.occurredEvents()
        carts.save(cart, version)
        cartEventPublisher.publishBatch(events)
    }

    override fun submitCart(command: SubmitCartCommand): Either<AppError, Unit> = either {
        val cartId = CartId(command.cartId)
        val cart = carts.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        val currentVersion = cart.version

        cart.submit(command.deliveryProvider, command.paymentServiceProvider).bind()
        val newCart = Cart.create(carts.nextIdentity())
        val newCartVersion = newCart.version

        val events = cart.occurredEvents() + newCart.occurredEvents()

        carts.save(cart, currentVersion)
        carts.save(newCart, newCartVersion)
        cartEventPublisher.publishBatch(events)
    }

    override fun addProductToCart(command: AddItemToCartCommand): Either<AppError, Unit> = either {
        val cartId = CartId(command.cartId)
        val cart = carts.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        val currentVersion = cart.version

        val productId = ProductId(command.productId)
        val product = products.findById(productId) ?: raise(ProductNotFoundError())

        cart.addItem(product.productId, command.quantity).bind()

        val events = cart.occurredEvents()
        carts.save(cart, currentVersion)

        cartEventPublisher.publishBatch(events)
    }

    override fun removeProductFromCart(command: RemoveItemFromCartCommand): Either<AppError, Unit> = either {
        val cartId = CartId(command.cartId)
        val cart = carts.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        val currentVersion = cart.version

        cart.removeItem(ProductId(command.productId)).bind()

        val events = cart.occurredEvents()
        carts.save(cart, currentVersion)

        cartEventPublisher.publishBatch(events)
    }
}