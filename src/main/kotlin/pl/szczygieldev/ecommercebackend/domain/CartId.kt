package pl.szczygieldev.ecommercebackend.domain


class CartId private constructor(val id:String){
    companion object{
        fun valueOf(id: String) : CartId{
            return  CartId(id)
        }
    }

}