package pl.szczygieldev.external.psp.model

import java.math.BigDecimal
import java.net.URL

internal class Payment(val id: String,val amount:BigDecimal,var amountPaid:BigDecimal, val url: URL,var status: PaymentStatus, val returnURL: URL){
    fun pay(amount: BigDecimal){
        amountPaid+=amount
        if(amountPaid==amount){
            status = PaymentStatus.PAID
        }else{
            status = PaymentStatus.INVALID_AMOUNT
        }
    }
}
