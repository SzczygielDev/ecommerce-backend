package pl.szczygieldev.ecommercebackend.domain.event

import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.shared.ddd.core.DomainEvent
import java.math.BigDecimal

sealed class OrderEvent : DomainEvent<OrderEvent>()

class OrderCreated(
    val orderId: OrderId,
    val cartId: CartId,
    val amount: BigDecimal,
    val paymentDetails: PaymentDetails,
    val deliveryProvider: DeliveryProvider,
) : OrderEvent() {
    override fun toString(): String {
        return "OrderCreated(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

class OrderAccepted(val orderId: OrderId) : OrderEvent() {
    override fun toString(): String {
        return "OrderAccepted(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

class OrderRejected(val orderId: OrderId) : OrderEvent() {
    override fun toString(): String {
        return "OrderRejected(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

class OrderCanceled(val orderId: OrderId) : OrderEvent() {
    override fun toString(): String {
        return "OrderCanceled(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

class OrderPackagingStarted(val orderId: OrderId) : OrderEvent() {
    override fun toString(): String {
        return "OrderPackagingStarted(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

class OrderPackaged(val orderId: OrderId, val parcelId: ParcelId, val  parcelDimensions: ParcelDimensions) : OrderEvent() {
    override fun toString(): String {
        return "OrderPackaged(id=$id occuredOn=$occurredOn orderId=$orderId parcelIdentifier=$parcelId parcelDimensions=$parcelDimensions)"
    }
}

class OrderDeliveryStatusChanged(val orderId: OrderId, val status: DeliveryStatus) : OrderEvent() {
    override fun toString(): String {
        return "OrderDeliveryStatusChanged(id=$id occuredOn=$occurredOn orderId=$orderId deliveryStatus=$status)"
    }
}

class OrderPaymentReceived(val orderId: OrderId, val paymentTransaction: PaymentTransaction) : OrderEvent() {
    override fun toString(): String {
        return "OrderPaymentReceived(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

class OrderPaid(val orderId: OrderId) : OrderEvent() {
    override fun toString(): String {
        return "OrderPaid(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

class OrderInvalidAmountPaid(val orderId: OrderId, val paidAmount: BigDecimal,val desiredAmount: BigDecimal) : OrderEvent() {
    override fun toString(): String {
        return "OrderInvalidAmountPaid(id=$id occuredOn=$occurredOn paidAmount=$paidAmount desiredAmount=$desiredAmount)"
    }
}