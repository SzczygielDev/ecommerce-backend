package pl.szczygieldev.product.infrastructure.adapter.`in`.api.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import pl.szczygieldev.product.domain.error.AppError
import pl.szczygieldev.product.domain.error.InfrastructureError
import pl.szczygieldev.product.domain.error.ProductNotFoundError

internal fun mapToError(error: AppError): ResponseEntity<*> {
    return when (error) {

        is ProductNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))

        is InfrastructureError -> when (error) {
            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, error.message))
        }

    }
}
