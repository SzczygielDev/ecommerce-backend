package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import pl.szczygieldev.ecommercebackend.domain.error.*

internal fun mapToError(error: AppError): ResponseEntity<*> {
    return when (error) {
        //region Cart
        is CartAlreadySubmittedError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))

        is CartNotActiveError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))

        is CartNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))
        //endregion

        //region Product
        is ProductNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))
        //endregion

        //region PriceCalculator
        is MissingProductForCalculateError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))
        is UnableToCalculateCartTotalError -> ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))
        //endregion
    }
}
