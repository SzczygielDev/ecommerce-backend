package pl.szczygieldev.ecommercebackend.usecase

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.CartService
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Carts
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.error.CartNotFoundError
import pl.szczygieldev.ecommercebackend.domain.error.ProductNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import pl.szczygieldev.shared.outbox.Outbox
import java.math.BigDecimal
import java.util.*

class CartUseCaseTests : FunSpec() {
    val productsMock = mockk<Products>()
    val cartsMock = mockk<Carts>()
    val eventPublisherMock = mockk<DomainEventPublisher<CartEvent>>()
    val productUseCase: CartUseCase = CartService(cartsMock,productsMock,eventPublisherMock)

    init {
        every { cartsMock.save(any(),any()) } just runs
        every { eventPublisherMock.publish(any()) } just runs
        every { eventPublisherMock.publishBatch(any()) } just runs

        test("Submitting cart should raise CartNotFoundError when cart not found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID().toString())
            every { cartsMock.findById(cartId)} returns null

            //Act
            val result = productUseCase.submitCart(SubmitCartCommand(cartId.id()))

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<CartNotFoundError>()
        }
        test("Submitting cart should call submit on it when cart found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID().toString())
            val cart =  spyk<Cart>(Cart.create(cartId))
            every { cartsMock.findById(cartId)} returns cart

            //Act
            productUseCase.submitCart(SubmitCartCommand(cartId.id()))

            //Assert
            verify { cart.submit() }
        }

        test("Adding product to cart should raise CartNotFoundError when cart not found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID().toString())
            val productId = ProductId(UUID.randomUUID().toString())
            every { cartsMock.findById(cartId)} returns null

            //Act
            val result = productUseCase.addProductToCart(AddItemToCartCommand(cartId.id(),productId.id(),1))

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<CartNotFoundError>()
        }

        test("Adding product to cart should raise ProductNotFoundError when product not found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID().toString())
            val productId = ProductId(UUID.randomUUID().toString())
            val cart = Cart.create(cartId)
            every { cartsMock.findById(cartId)} returns cart
            every { productsMock.findById(productId) } returns null

            //Act
            val result = productUseCase.addProductToCart(AddItemToCartCommand(cartId.id(),productId.id(),1))

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<ProductNotFoundError>()
        }

        test("Adding product to cart should call addItem with found product") {
            //Arrange
            val cartId = CartId(UUID.randomUUID().toString())
            val cart = spyk<Cart>(Cart.create(cartId))
            val productId = ProductId(UUID.randomUUID().toString())
            val product = Product.create(
                productId,
                ProductTitle("product A"),
                ProductDescription("description"),
                ProductPrice(
                    BigDecimal.valueOf(250)
                )
            )
            every { cartsMock.findById(cartId)} returns cart
            every { productsMock.findById(productId) } returns product
            val command = AddItemToCartCommand(cartId.id(),productId.id(),1)

            //Act
            val result = productUseCase.addProductToCart(command)

            //Assert
            verify { cart.addItem(productId,1) }
        }

        test("Removing product from cart should raise CartNotFoundError when cart not found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID().toString())
            val productId = ProductId(UUID.randomUUID().toString())
            every { cartsMock.findById(cartId)} returns null

            //Act
            val result = productUseCase.removeProductFromCart(RemoveItemFromCartCommand(cartId.id(),productId.id(),))

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<CartNotFoundError>()
        }

        test("Removing product from cart should call removeItem on it when cart found"){
            //Arrange
            val cartId = CartId(UUID.randomUUID().toString())
            val cart = spyk<Cart>(Cart.create(cartId))
            val productId = ProductId(UUID.randomUUID().toString())
            val product = Product.create(
                productId,
                ProductTitle("product A"),
                ProductDescription("description"),
                ProductPrice(
                    BigDecimal.valueOf(250)
                )
            )
            every { cartsMock.findById(cartId)} returns cart
            every { productsMock.findById(productId) } returns product
            val command = RemoveItemFromCartCommand(cartId.id(),productId.id())

            //Act
            val result = productUseCase.removeProductFromCart(command)

            //Assert
            verify { cart.removeItem(productId) }
        }
    }
}