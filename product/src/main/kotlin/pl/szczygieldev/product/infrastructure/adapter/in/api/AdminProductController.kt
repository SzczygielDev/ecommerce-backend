package pl.szczygieldev.product.infrastructure.adapter.`in`.api

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.product.application.port.`in`.query.GetAllProductsQuery
import pl.szczygieldev.product.application.port.`in`.query.GetPaginatedProductsQuery
import pl.szczygieldev.product.application.port.`in`.query.GetProductByIdQuery
import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.domain.ProductId
import pl.szczygieldev.product.domain.error.AppError
import pl.szczygieldev.product.domain.error.ProductNotFoundError
import pl.szczygieldev.product.infrastructure.adapter.`in`.api.advice.mapToError
import pl.szczygieldev.product.infrastructure.adapter.`in`.api.presenter.ProductPresenter
import pl.szczygieldev.product.infrastructure.adapter.`in`.api.resource.ProductFullDto
import java.util.*


@RequestMapping("/admin/products")
@RestController
internal class AdminProductController(
    val productPresenter: ProductPresenter,
    val mediator: Mediator
) {
    @GetMapping
    suspend fun getAll(
        @RequestParam(required = false) offset: Long?,
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<List<ProductFullDto>> {
        if (offset != null && limit != null) {
            return ResponseEntity.ok()
                .body(mediator.send(GetPaginatedProductsQuery(offset, limit)).map { productPresenter.toFullDto(it) })
        }

        return ResponseEntity.ok().body(mediator.send(GetAllProductsQuery()).map { productPresenter.toFullDto(it) })
    }

    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: UUID): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = ProductId(id)
            mediator.send(GetProductByIdQuery(productId)) ?: raise(ProductNotFoundError(productId.id.toString()))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toFullDto(product)) })
    }
}