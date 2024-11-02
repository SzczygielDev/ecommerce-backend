package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.shared.ddd.core.Identity

data class ImageId(val id: String) : Identity<ImageId>(id)