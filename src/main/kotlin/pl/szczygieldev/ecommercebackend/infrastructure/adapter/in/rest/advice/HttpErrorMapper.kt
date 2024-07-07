package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import pl.szczygieldev.ecommercebackend.domain.error.*
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.error.CommandNotFoundError

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

        //region Order
        is AlreadyAcceptedOrderError -> TODO()
        is CannotCancelSentOrderError -> TODO()
        is CannotPackageNotAcceptedOrderError -> TODO()
        is CannotReturnNotReceivedOrderError -> TODO()
        is InvalidPaymentAmountError -> TODO()
        is NotPaidOrderError -> TODO()
        is OrderNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))
        is CannotRegisterParcelError -> TODO()
        //endregion

        is InfrastructureError -> when (error) {
            is CommandNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.message))
            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, error.message))
        }
    }
}
