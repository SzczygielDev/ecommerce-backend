package pl.szczygieldev.product.infrastructure.adapter.`in`.rest.advice

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.RuntimeException

@RestControllerAdvice
@Component("productModule.GenericControllerAdvice")
internal class GenericControllerAdvice {
    companion object {
        private val log = KotlinLogging.logger { }
    }



   @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ProblemDetail {
        val problemDetail: ProblemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.message ?: "")
        problemDetail.title = "Unknown error ${ex.javaClass.name}"

        return problemDetail.also { log.error {  "Controller advice handling exception='${ex.javaClass.name}' message='${ex.message}'"} }
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ProblemDetail {
        val problemDetail: ProblemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "")
        problemDetail.title = "Provided invalid argument"

        return problemDetail.also { log.error {  "Controller advice handling exception='${ex.javaClass.name}' message='${ex.message}'"} }
    }
}