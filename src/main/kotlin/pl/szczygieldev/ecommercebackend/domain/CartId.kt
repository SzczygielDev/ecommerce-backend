package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.ddd.core.Identity


class CartId (val id:String): Identity<CartId>(id)