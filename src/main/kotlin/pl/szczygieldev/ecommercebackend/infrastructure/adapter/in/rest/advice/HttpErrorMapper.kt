package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import pl.szczygieldev.ecommercebackend.domain.error.*
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.error.CommandAlreadyProcessingError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.error.CommandNotFoundError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.error.ImageUploadError

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
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))

        is UnableToCalculateCartTotalError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, error.message))
        //endregion

        //region Order
        is AlreadyAcceptedOrderError ->  ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.message))
        is CannotCancelSentOrderError -> ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.message))
        is CannotPackageNotAcceptedOrderError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))
        is CannotReturnNotReceivedOrderError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))
        is InvalidPaymentAmountError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))
        is NotPaidOrderError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))
        is OrderNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))
        is CannotRegisterParcelError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, error.message))
        is PackingNotInProgressError -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.message))
        //endregion

        //region Infrastructure errors
        is InfrastructureError -> when (error) {
            is CommandNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))
            is CommandAlreadyProcessingError -> ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.message))
            is ImageUploadError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, error.message))
            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, error.message))
        }
        //endregion
    }
}
