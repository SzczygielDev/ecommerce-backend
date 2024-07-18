package pl.szczygieldev.ecommercebackend.infrastructure.integration.payments

import pl.szczygieldev.ecommercebackend.domain.PaymentId
import java.math.BigDecimal
import java.net.URL

class Payment(val id: PaymentId,val amount:BigDecimal,var amountPaid:BigDecimal, val url: URL,var status: PaymentStatus){
    fun pay(amount: BigDecimal){
        amountPaid+=amount
        if(amountPaid==amount){
            status = PaymentStatus.PAID
        }else{
            status = PaymentStatus.INVALID_AMOUNT
        }
    }
}
