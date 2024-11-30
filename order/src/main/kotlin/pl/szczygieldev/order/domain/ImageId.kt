package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.Identity


data class ImageId(val id: String) : Identity<ImageId>(id)