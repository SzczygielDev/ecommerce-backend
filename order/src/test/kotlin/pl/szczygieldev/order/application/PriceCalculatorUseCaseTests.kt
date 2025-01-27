package pl.szczygieldev.order.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.query.model.CartProjection
import pl.szczygieldev.order.application.port.`in`.PriceCalculatorUseCase
import pl.szczygieldev.order.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.order.application.port.out.CartsProjections
import pl.szczygieldev.order.application.port.out.Products
import pl.szczygieldev.order.domain.*
import pl.szczygieldev.order.domain.error.CartNotFoundError
import pl.szczygieldev.order.domain.error.UnableToCalculateCartTotalError
import pl.szczygieldev.order.domain.event.CartTotalRecalculated
import pl.szczygieldev.order.domain.event.PriceCalculatorEvent
import java.math.BigDecimal
import java.util.*


internal class PriceCalculatorUseCaseTests() : FunSpec() {
    val productsMock = mockk<Products>()
    val cartsProjectionsMock = mockk<CartsProjections>()
    val eventPublisherMock = mockk<DomainEventPublisher<PriceCalculatorEvent>>()

    val priceCalculatorUseCase: PriceCalculatorUseCase = PriceCalculatorService(
        productsMock,
        cartsProjectionsMock,
        eventPublisherMock
    )

    init {
        test("Calculating cart total for non existing cart should raise CartNotFoundError") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val emptyCart = CartProjection(cartId, CartStatus.ACTIVE, BigDecimal.ZERO, emptyList())

            every { cartsProjectionsMock.findById(cartId) } returns emptyCart
            every { cartsProjectionsMock.findById(any()) } returns null

            //Act
            val result = priceCalculatorUseCase.calculateCartTotal(
                CalculateCartTotalCommand(
                    CartId(UUID.randomUUID())
                )
            )

            //Assert
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<CartNotFoundError>()
        }

        test("Calculating cart total when products cannot be found should raise UnableToCalculateCartTotalError") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val productId = ProductId(UUID.randomUUID())
            val cart =
                CartProjection(cartId, CartStatus.ACTIVE, BigDecimal.ZERO, listOf(CartProjection.Entry(productId, 1)))

            every { cartsProjectionsMock.findById(cartId) } returns cart
            every { productsMock.findById(productId) } returns null

            //Act
            val result = priceCalculatorUseCase.calculateCartTotal(
                CalculateCartTotalCommand(
                    cartId
                )
            )

            //Assert
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<UnableToCalculateCartTotalError>()
        }

        test("Calculating cart total should return correct amount inside CartTotalRecalculated event") {
            //Arrange
            val productA = Product(
                ProductId(UUID.randomUUID()),
                "product A",
                BigDecimal.valueOf(250),
                ImageId(UUID.randomUUID().toString())
            )
            val productB = Product(
                ProductId(UUID.randomUUID()),
                "product B",
                BigDecimal.valueOf(500),
                ImageId(UUID.randomUUID().toString())
            )
            val cartId = CartId(UUID.randomUUID())
            val cart = CartProjection(
                cartId,
                CartStatus.ACTIVE,
                BigDecimal.ZERO,
                listOf(CartProjection.Entry(productA.productId, 4), CartProjection.Entry(productB.productId, 2))
            )

            every { cartsProjectionsMock.findById(cartId) } returns cart
            every { productsMock.findById(productA.productId) } returns productA
            every { productsMock.findById(productB.productId) } returns productB

            val eventSlot = slot<PriceCalculatorEvent>()
            every { eventPublisherMock.publish(capture(eventSlot)) } just runs

            //Act
            val result = priceCalculatorUseCase.calculateCartTotal(
                CalculateCartTotalCommand(
                    cartId
                )
            )

            //Assert
            result.isRight().shouldBe(true)
            val capturedEvent = eventSlot.captured
            capturedEvent.shouldBeInstanceOf<CartTotalRecalculated>()
            capturedEvent.cartId.shouldBe(cartId)
            capturedEvent.amount.shouldBe(BigDecimal.valueOf(2000))
        }

    }
}