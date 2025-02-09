package pl.szczygieldev.product.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import pl.szczygieldev.product.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.ImageId
import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.domain.ProductDescription
import pl.szczygieldev.product.domain.ProductId
import pl.szczygieldev.product.domain.ProductPrice
import pl.szczygieldev.product.domain.ProductTitle
import java.math.BigDecimal
import java.util.*

internal class CreateProductCommandHandlerTests  : FunSpec() {
    val productsMock = mockk<Products>()
    val createProductCommandHandler = CreateProductCommandHandler(productsMock)

    init {
        test("Product should be saved on create") {
            //Arrange
            val imageId = ImageId(UUID.randomUUID())
            val productId = ProductId(UUID.randomUUID())
            val command = CreateProductCommand(productId, "Product A", "description", 100.0, imageId)


            val savedProduct = slot<Product>()
            every { productsMock.save(capture(savedProduct), any()) } returns Product.create(
                productId, ProductTitle(command.title),
                ProductDescription(command.description), ProductPrice(BigDecimal.valueOf(command.price)), imageId
            )
            every { productsMock.nextIdentity() } returns productId

            //Act
            val result = createProductCommandHandler.handle(command)

            //Assert
            savedProduct.captured.productId.sameValueAs(productId).shouldBe(true)
            verify { productsMock.save(any(), any()) }
        }
    }
}