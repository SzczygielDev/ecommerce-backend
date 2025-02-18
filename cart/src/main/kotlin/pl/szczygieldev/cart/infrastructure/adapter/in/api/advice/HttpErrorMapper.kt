package pl.szczygieldev.cart.infrastructure.adapter.`in`.api.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import pl.szczygieldev.cart.domain.*
import pl.szczygieldev.cart.domain.AppError
import pl.szczygieldev.cart.domain.CartAlreadySubmittedError
import pl.szczygieldev.cart.domain.CartNotActiveError
import pl.szczygieldev.cart.domain.CartNotFoundError
import pl.szczygieldev.cart.domain.InfrastructureError


internal fun mapToError(error: AppError): ResponseEntity<*> {
    return when (error) {
        //region Cart
        is CartAlreadySubmittedError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))

        is CartNotActiveError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))

        is CartNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))
        is ProductNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))
        //region Infrastructure errors
        is InfrastructureError -> when (error) {

            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, error.message))
        }
        //endregion
        //region PriceCalculator
        is MissingProductForCalculateError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))

        is UnableToCalculateCartTotalError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, error.message))
        //endregion

    }
}
