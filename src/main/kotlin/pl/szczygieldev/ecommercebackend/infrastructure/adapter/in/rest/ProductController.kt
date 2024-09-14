package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.ProductNotFoundError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter.ProductPresenter
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.CreateProductRequest
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.ProductDto
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.UpdateProductRequest

@RequestMapping("/products")
@RestController
class ProductController(
    val productUseCase: ProductUseCase,
    val products: Products,
    val productPresenter: ProductPresenter
) {
    @GetMapping
    fun getAll(): ResponseEntity<List<ProductDto>> =
        ResponseEntity.ok().body(products.findAll().map { productPresenter.toDto(it) })

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = ProductId(id)
            products.findById(productId) ?: raise(ProductNotFoundError(productId.id))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toDto(product)) })
    }

    @PostMapping
    fun create(@RequestBody request: CreateProductRequest): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(
            productPresenter.toDto(
                productUseCase.create(
                    CreateProductCommand(
                        request.title,
                        request.description,
                        request.price
                    )
                )
            )
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = ProductId(id)
            products.delete(productId) ?: raise(ProductNotFoundError(productId.id))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toDto(product)) })
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody request: UpdateProductRequest): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = ProductId(id)
            productUseCase.update(
                UpdateProductCommand(
                    productId,
                    ProductTitle(request.title),
                    ProductDescription(request.description),
                    ProductPrice(request.price)
                )
            )
            products.findById(productId) ?: raise(ProductNotFoundError(productId.id))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toDto(product)) })
    }
}
