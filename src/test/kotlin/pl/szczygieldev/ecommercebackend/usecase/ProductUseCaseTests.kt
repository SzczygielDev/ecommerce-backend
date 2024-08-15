package pl.szczygieldev.ecommercebackend.usecase

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.ProductService
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.ecommercebackend.domain.event.PriceCalculatorEvent
import pl.szczygieldev.ecommercebackend.domain.event.ProductEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.math.BigDecimal
import java.util.*

class ProductUseCaseTests : FunSpec() {
    val productsMock = mockk<Products>()
    val eventPublisherMock = mockk<DomainEventPublisher<ProductEvent>>()
    val productUseCase: ProductUseCase = ProductService(productsMock, eventPublisherMock)

    init {
        test("Product should be saved on create") {
            //Arrange
            val command = CreateProductCommand("Product A", "description", 100.0)
            val productId = ProductId(UUID.randomUUID().toString())

            val savedProduct = slot<Product>()
            coEvery { productsMock.save(capture(savedProduct), any()) } returns Product.create(
                productId, ProductTitle(command.title),
                ProductDescription(command.description), ProductPrice(BigDecimal.valueOf(command.price))
            )
            every { productsMock.nextIdentity() } returns productId

            //Act
            val result = productUseCase.create(command)

            //Assert
            result.productId.sameValueAs(productId).shouldBe(true)
            savedProduct.captured.productId.sameValueAs(productId).shouldBe(true)
            verify { productsMock.save(any(), any()) }
        }
    }
}