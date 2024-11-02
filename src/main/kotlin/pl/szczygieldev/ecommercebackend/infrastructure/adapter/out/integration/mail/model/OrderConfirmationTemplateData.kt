package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.integration.mail.model

data class OrderConfirmationTemplateData(val name: String, val orderId: String, val total: String, val date: String, val products: List<ProductItem>) {
    data class ProductItem(val productName: String, val productQuantity: Int, val amount: String)
}