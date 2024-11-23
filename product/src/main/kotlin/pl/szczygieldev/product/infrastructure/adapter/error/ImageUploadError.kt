package pl.szczygieldev.product.infrastructure.adapter.error

import pl.szczygieldev.product.domain.error.InfrastructureError

class ImageUploadError(message: String) : InfrastructureError(message){
    companion object {
        fun mediaTypeNotFound(): ImageUploadError {
            return ImageUploadError("Failed to determine media type")
        }

        fun mediaTypeNotSupported(): ImageUploadError {
            return ImageUploadError("Unsupported media type")
        }

        fun storageError(): ImageUploadError {
            return ImageUploadError("Error while uploading image")
        }
    }
}