package pl.szczygieldev.order.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.Identity


internal data class ImageId(val id: String) : Identity<ImageId>(id)