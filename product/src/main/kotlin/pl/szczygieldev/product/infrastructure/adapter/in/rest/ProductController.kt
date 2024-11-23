package pl.szczygieldev.product.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.product.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.product.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.*
import pl.szczygieldev.product.domain.error.AppError
import pl.szczygieldev.product.domain.error.ProductNotFoundError
import pl.szczygieldev.product.infrastructure.adapter.`in`.command.MediatorFacade
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.presenter.ProductPresenter
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.resource.CreateProductRequest
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.resource.ProductDto
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.resource.UpdateProductRequest

@RequestMapping("/products")
@RestController
class ProductController(
    val mediator: MediatorFacade,
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
    suspend fun create(@RequestBody request: CreateProductRequest): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = products.nextIdentity()
            mediator.send(
                CreateProductCommand(
                    productId,
                    request.title,
                    request.description,
                    request.price,
                    ImageId(request.imageId)
                )
            )
            products.findById(productId) ?: raise(ProductNotFoundError(productId.id))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toDto(product)) })
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = ProductId(id)
            products.delete(productId) ?: raise(ProductNotFoundError(productId.id))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toDto(product)) })
    }

    @PutMapping("/{id}")
    suspend fun update(@PathVariable id: String, @RequestBody request: UpdateProductRequest): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = ProductId(id)
            mediator.send(
                UpdateProductCommand(
                    productId,
                    ProductTitle(request.title),
                    ProductDescription(request.description),
                    ProductPrice(request.price),
                    ImageId(request.imageId)
                )
            )
            products.findById(productId) ?: raise(ProductNotFoundError(productId.id))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toDto(product)) })
    }
}
