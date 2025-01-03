package pl.szczygieldev.product.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.application.port.`in`.ProductUseCase
import pl.szczygieldev.product.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.product.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.product.domain.*
import pl.szczygieldev.product.domain.error.ProductNotFoundError
import pl.szczygieldev.product.domain.event.ProductEvent
import java.math.BigDecimal
import java.util.*

internal class ProductUseCaseTests : FunSpec() {
    val productsMock = mockk<Products>()
    val eventPublisherMock = mockk<DomainEventPublisher<ProductEvent>>()
    val productUseCase: ProductUseCase = ProductService(productsMock, eventPublisherMock)

    init {
        context("Product create tests") {
            test("Product should be saved on create") {
                //Arrange
                val imageId = ImageId(UUID.randomUUID().toString())
                val productId = ProductId(UUID.randomUUID().toString())
                val command = CreateProductCommand(productId,"Product A", "description", 100.0, imageId)


                val savedProduct = slot<Product>()
                every { productsMock.save(capture(savedProduct), any()) } returns Product.create(
                    productId, ProductTitle(command.title),
                    ProductDescription(command.description), ProductPrice(BigDecimal.valueOf(command.price)), imageId
                )
                every { productsMock.nextIdentity() } returns productId

                //Act
                val result = productUseCase.create(command)

                //Assert
                savedProduct.captured.productId.sameValueAs(productId).shouldBe(true)
                verify { productsMock.save(any(), any()) }
            }
        }

        context("Product update tests") {
            val productId = ProductId(UUID.randomUUID().toString())
            val imageId = ImageId(UUID.randomUUID().toString())
            val product = Product.create(
                productId, ProductTitle("Product title"),
                ProductDescription("Product description"),
                ProductPrice(BigDecimal.TEN), imageId
            )
            val newTitle = ProductTitle("Product new title")
            val newDescription = ProductDescription("Product new description")
            val newPrice = ProductPrice(BigDecimal.valueOf(20))
            val newImageId = ImageId(UUID.randomUUID().toString())

            val command = UpdateProductCommand(productId, newTitle, newDescription, newPrice, newImageId)
            test("Product update should raise ProductNotFoundError when product was not found") {
                //Arrange
                every { productsMock.findById(productId) } returns null

                //Act
                val result = productUseCase.update(command)

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
                productUseCase.update(command)

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
                productUseCase.update(command)

                //Assert
                verify { productsMock.save(product, any()) }
            }

            test("Product occurred events should be published when no error occurred") {
                //Arrange
                every { productsMock.findById(productId) } returns product
                every { productsMock.save(any(), any()) } returns product
                every { eventPublisherMock.publishBatch(any()) } just runs

                //Act
                productUseCase.update(command)

                //Assert
                verify { eventPublisherMock.publishBatch(product.occurredEvents()) }
            }
        }
    }
}