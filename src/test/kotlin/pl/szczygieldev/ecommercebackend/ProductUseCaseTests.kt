package pl.szczygieldev.ecommercebackend

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.ProductService
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.ProductId
import java.util.*

class ProductUseCaseTests : FunSpec() {
    val productsMock = mockk<Products>()
    val productUseCase: ProductUseCase = ProductService(productsMock)

    init {
        test("Product should be saved on create") {
            //Arrange
            val command = CreateProductCommand("Product A", "description", 100.0)
            val productId = ProductId(UUID.randomUUID().toString())

            every { productsMock.save(any()) } just runs
            every { productsMock.nextIdentity() } returns productId

            //Act
            val result = productUseCase.create(command)

            //Assert
            result.productId.sameValueAs(productId).shouldBe(true)
            verify { productsMock.save(any()) }
        }
    }
}