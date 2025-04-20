package pl.szczygieldev.product.infrastructure.adapter.`in`.api.error

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity

internal abstract class ImageUploadError {
    companion object {
        fun mediaTypeNotFound(): ResponseEntity<*> {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to determine media type"
                    )
                )
        }

        fun mediaTypeNotSupported(): ResponseEntity<*> {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unsupported media type"))
        }

        fun storageError(): ResponseEntity<*> {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error while uploading image"
                    )
                )

        }
    }
}