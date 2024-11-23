package pl.szczygieldev.order.infrastructure.adapter.error

import pl.szczygieldev.order.domain.error.InfrastructureError

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