package pl.szczygieldev.product.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.product.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.ImageId
import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.domain.ProductDescription
import pl.szczygieldev.product.domain.ProductId
import pl.szczygieldev.product.domain.ProductPrice
import pl.szczygieldev.product.domain.ProductTitle
import pl.szczygieldev.product.domain.error.ProductNotFoundError
import pl.szczygieldev.product.domain.event.ProductEvent
import java.math.BigDecimal
import java.util.*

internal class UpdateProductCommandHandlerTests : FunSpec() {
    val productsMock = mockk<Products>()
    val eventPublisherMock = mockk<DomainEventPublisher<ProductEvent>>()
    val updateProductCommandHandler = UpdateProductCommandHandler(productsMock, eventPublisherMock)

    init {
        val productId = ProductId(UUID.randomUUID())
        val imageId = ImageId(UUID.randomUUID())
        val product = Product.create(
            productId, ProductTitle("Product title"),
            ProductDescription("Product description"),
            ProductPrice(BigDecimal.TEN), imageId
        )
        val newTitle = ProductTitle("Product new title")
        val newDescription = ProductDescription("Product new description")
        val newPrice = ProductPrice(BigDecimal.valueOf(20))
        val newImageId = ImageId(UUID.randomUUID())

        val command = UpdateProductCommand(productId, newTitle, newDescription, newPrice, newImageId)
        test("Product update should raise ProductNotFoundError when product was not found") {
            //Arrange
            every { productsMock.findById(productId) } returns null

            //Act
            val result = updateProductCommandHandler.handle(command)

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<ProductNotFoundError>()
        }

        test("Product title, description, price and image should be updated") {
            //Arrange
            every { productsMock.findById(productId) } returns product
            val savedProductSlot = slot<Product>()
            every { productsMock.save(capture(savedProductSlot), any()) } returns product
            every { eventPublisherMock.publishBatch(any()) } just runs

            //Act
            updateProductCommandHandler.handle(command)

            //Assert
            val updatedProduct = savedProductSlot.captured

            updatedProduct.title.value.shouldBeEqual(newTitle.value)
            updatedProduct.description.content.shouldBeEqual(newDescription.content)
            updatedProduct.price.amount.compareTo(newPrice.amount).shouldBe(0)
            updatedProduct.imageId.sameValueAs(newImageId).shouldBe(true)
        }

        test("Product should be saved when no error occurred") {
            //Arrange
            every { productsMock.findById(productId) } returns product
            every { productsMock.save(any(), any()) } returns product
            every { eventPublisherMock.publishBatch(any()) } just runs

            //Act
            updateProductCommandHandler.handle(command)

            //Assert
            verify { productsMock.save(product, any()) }
        }

        test("Product occurred events should be published when no error occurred") {
            //Arrange
            every { productsMock.findById(productId) } returns product
            every { productsMock.save(any(), any()) } returns product
            every { eventPublisherMock.publishBatch(any()) } just runs

            //Act
            updateProductCommandHandler.handle(command)

            //Assert
            verify { eventPublisherMock.publishBatch(product.occurredEvents()) }
        }

    }
}