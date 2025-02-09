package pl.szczygieldev.order.domain.event

import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEvent
import pl.szczygieldev.order.domain.*
import java.math.BigDecimal

internal sealed class OrderEvent : DomainEvent<OrderEvent>()

internal class OrderCreated(
    val orderId: OrderId,
    val cartId: CartId,
    val amount: BigDecimal,
    val paymentDetails: PaymentDetails,
    val deliveryProvider: DeliveryProvider,
    val items : List<Order.OrderItem>
) : OrderEvent() {
    override fun toString(): String {
        return "OrderCreated(id=$id occuredOn=$occurredOn orderId=$orderId cartId=$cartId amount=$amount paymentDetails=$paymentDetails deliveryProvider=$deliveryProvider items=$items)"
    }
}

internal class OrderAccepted(val orderId: OrderId) : OrderEvent() {
    override fun toString(): String {
        return "OrderAccepted(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

internal class OrderRejected(val orderId: OrderId) : OrderEvent() {
    override fun toString(): String {
        return "OrderRejected(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

internal class OrderCanceled(val orderId: OrderId) : OrderEvent() {
    override fun toString(): String {
        return "OrderCanceled(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

internal class OrderPackagingStarted(val orderId: OrderId) : OrderEvent() {
    override fun toString(): String {
        return "OrderPackagingStarted(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

internal class OrderPackaged(val orderId: OrderId, val parcelId: ParcelId, val  parcelDimensions: ParcelDimensions) : OrderEvent() {
    override fun toString(): String {
        return "OrderPackaged(id=$id occuredOn=$occurredOn orderId=$orderId parcelIdentifier=$parcelId parcelDimensions=$parcelDimensions)"
    }
}

internal class OrderDeliveryStatusChanged(val orderId: OrderId, val status: DeliveryStatus) : OrderEvent() {
    override fun toString(): String {
        return "OrderDeliveryStatusChanged(id=$id occuredOn=$occurredOn orderId=$orderId deliveryStatus=$status)"
    }
}

internal class OrderPaymentReceived(val orderId: OrderId, val paymentTransaction: PaymentTransaction) : OrderEvent() {
    override fun toString(): String {
        return "OrderPaymentReceived(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

internal class OrderPaid(val orderId: OrderId) : OrderEvent() {
    override fun toString(): String {
        return "OrderPaid(id=$id occuredOn=$occurredOn orderId=$orderId)"
    }
}

internal class OrderInvalidAmountPaid(val orderId: OrderId, val paidAmount: BigDecimal,val desiredAmount: BigDecimal) : OrderEvent() {
    override fun toString(): String {
        return "OrderInvalidAmountPaid(id=$id occuredOn=$occurredOn paidAmount=$paidAmount desiredAmount=$desiredAmount)"
    }
}