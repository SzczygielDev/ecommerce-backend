package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.CartUseCase
import pl.szczygieldev.order.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.order.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.order.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.order.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.order.application.port.out.Carts
import pl.szczygieldev.order.application.port.out.Products
import pl.szczygieldev.order.domain.Cart
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.ProductId
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.CartNotFoundError
import pl.szczygieldev.order.domain.error.ProductNotFoundError
import pl.szczygieldev.order.domain.event.CartEvent

@UseCase
class CartService(
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

        val events = cart.occurredEvents()
        carts.save(cart, currentVersion)

        cartEventPublisher.publishBatch(events)
    }

    override fun addProductToCart(command: AddItemToCartCommand): Either<AppError, Unit> = either {
        val cartId = CartId(command.cartId)
        val cart = carts.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        val currentVersion = cart.version

        val productId = ProductId(command.productId)
        val product = products.findById(productId) ?: raise(ProductNotFoundError.forId(productId))

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