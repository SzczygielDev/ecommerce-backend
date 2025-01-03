package pl.szczygieldev.order.domain

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import pl.szczygieldev.order.domain.error.*
import pl.szczygieldev.order.domain.event.*
import java.math.BigDecimal
import java.net.URL
import java.time.Instant
import java.util.*

internal class OrderTests : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("Order state changes") {
        given("a new order") {
            val cartId = CartId(UUID.randomUUID().toString())
            val orderId = OrderId(UUID.randomUUID().toString())
            val paymentId = PaymentId(UUID.randomUUID().toString())
            val orderAmount = BigDecimal.TEN
            val order = Order.create(
                orderId,
                cartId,
                PaymentDetails(
                    paymentId, orderAmount, URL("http://localhost:8080/"),
                    PaymentServiceProvider.MOCK_PSP
                ),
                DeliveryProvider.MOCK_DELIVERY_PROVIDER,
                emptyList()
            )
            val dimensions = ParcelDimensions(10.0, 25.0, 30.0, 5.0)
            val parcelId = ParcelId(UUID.randomUUID().toString())

            then("status is ${OrderStatus.CREATED}") {
                order.status.shouldBe(OrderStatus.CREATED)
            }
            then("can be accepted") {
                val result = order.accept()
                result.isRight().shouldBe(true)
                order.occurredEvents().filterIsInstance<OrderAccepted>().shouldNotBeEmpty()
            }
            then("can be rejected") {
                val result = order.reject()
                result.isRight().shouldBe(true)
                order.occurredEvents().filterIsInstance<OrderRejected>().shouldNotBeEmpty()
            }

            then("can be canceled") {
                val result = order.cancel()
                result.isRight().shouldBe(true)
                order.occurredEvents().filterIsInstance<OrderCanceled>().shouldNotBeEmpty()
            }
            then("packing process cannot start") {
                val result = order.beginPacking()
                result.isLeft().shouldBe(true)
                result.leftOrNull().shouldBeInstanceOf<CannotPackageNotAcceptedOrderError>()
            }
            then("packing process cannot be completed") {
                val result = order.completePacking(parcelId, dimensions)
                result.isLeft().shouldBe(true)
                result.leftOrNull().shouldBeInstanceOf<PackingNotInProgressError>()
            }
        }

        given("an accepted order") {
            val cartId = CartId(UUID.randomUUID().toString())
            val orderId = OrderId(UUID.randomUUID().toString())
            val paymentId = PaymentId(UUID.randomUUID().toString())
            val orderAmount = BigDecimal.TEN
            val order = Order.create(
                orderId,
                cartId,
                PaymentDetails(
                    paymentId, orderAmount, URL("http://localhost:8080/"),
                    PaymentServiceProvider.MOCK_PSP
                ),
                DeliveryProvider.MOCK_DELIVERY_PROVIDER,
                emptyList()
            )
            val paymentTransactionId = PaymentTransactionId(UUID.randomUUID().toString())
            val paymentTransaction = PaymentTransaction(
                paymentTransactionId,
                orderAmount,
                Instant.now()
            )
            order.accept()

            then("status is ${OrderStatus.ACCEPTED}") {
                order.status.shouldBe(OrderStatus.ACCEPTED)
            }
            then("it cannot be accepted again") {
                val result = order.accept()
                result.isLeft().shouldBe(true)
                result.leftOrNull().shouldBeInstanceOf<AlreadyAcceptedOrderError>()
            }
            then("it cannot be rejected") {
                val result = order.accept()
                result.isLeft().shouldBe(true)
                result.leftOrNull().shouldBeInstanceOf<AlreadyAcceptedOrderError>()
            }
            then("can be paid") {
                order.pay(
                    paymentTransaction
                )
                order.occurredEvents().filterIsInstance<OrderPaymentReceived>().shouldNotBeEmpty()
            }
            then("can be canceled") {
                val result = order.cancel()
                result.isRight().shouldBe(true)
                order.occurredEvents().filterIsInstance<OrderCanceled>().shouldNotBeEmpty()
            }
            `when`("its paid") {
                order.pay(
                    paymentTransaction
                )
                then("OrderPaymentReceived event its raised") {
                    order.occurredEvents().filterIsInstance<OrderPaymentReceived>().shouldNotBeEmpty()
                }
                then("packing process can begin") {
                    val result = order.beginPacking()
                    result.isRight().shouldBe(true)
                    order.occurredEvents().filterIsInstance<OrderPackagingStarted>().shouldNotBeEmpty()
                }
            }
            `when`("its not paid") {
                then("packing process cannot start") {
                    val result = order.beginPacking()
                    result.isLeft().shouldBe(true)
                    result.leftOrNull().shouldBeInstanceOf<NotPaidOrderError>()
                }
            }
        }

        given("an rejected order") {
            val cartId = CartId(UUID.randomUUID().toString())
            val orderId = OrderId(UUID.randomUUID().toString())
            val paymentId = PaymentId(UUID.randomUUID().toString())
            val orderAmount = BigDecimal.TEN
            val order = Order.create(
                orderId,
                cartId,
                PaymentDetails(
                    paymentId, orderAmount, URL("http://localhost:8080/"),
                    PaymentServiceProvider.MOCK_PSP
                ),
                DeliveryProvider.MOCK_DELIVERY_PROVIDER,
                emptyList()
            )

            order.reject()

            then("status is ${OrderStatus.REJECTED}") {
                order.status.shouldBe(OrderStatus.REJECTED)
            }

            then("packing process cannot start") {
                val result = order.beginPacking()
                result.isLeft().shouldBe(true)
                result.leftOrNull().shouldBeInstanceOf<CannotPackageNotAcceptedOrderError>()
            }
        }

        given("an order in packing process") {
            val cartId = CartId(UUID.randomUUID().toString())
            val orderId = OrderId(UUID.randomUUID().toString())
            val paymentId = PaymentId(UUID.randomUUID().toString())
            val orderAmount = BigDecimal.TEN
            val order = Order.create(
                orderId,
                cartId,
                PaymentDetails(
                    paymentId, orderAmount, URL("http://localhost:8080/"),
                    PaymentServiceProvider.MOCK_PSP
                ),
                DeliveryProvider.MOCK_DELIVERY_PROVIDER,
                emptyList()
            )
            val paymentTransactionId = PaymentTransactionId(UUID.randomUUID().toString())
            val paymentTransaction = PaymentTransaction(
                paymentTransactionId,
                orderAmount,
                Instant.now()
            )
            val dimensions = ParcelDimensions(10.0, 25.0, 30.0, 5.0)
            val parcelId = ParcelId(UUID.randomUUID().toString())

            order.accept()
            order.pay(
                paymentTransaction
            )
            order.beginPacking()

            then("status is ${OrderStatus.IN_PROGRESS}") {
                order.status.shouldBe(OrderStatus.IN_PROGRESS)
            }

            then("packing process can be completed") {
                val result = order.completePacking(parcelId, dimensions)

                result.isRight().shouldBe(true)
                order.occurredEvents().filterIsInstance<OrderPackaged>().shouldNotBeEmpty()
            }

        }

        given("an order after completing packing process") {
            val cartId = CartId(UUID.randomUUID().toString())
            val orderId = OrderId(UUID.randomUUID().toString())
            val paymentId = PaymentId(UUID.randomUUID().toString())
            val orderAmount = BigDecimal.TEN
            val order = Order.create(
                orderId,
                cartId,
                PaymentDetails(
                    paymentId, orderAmount, URL("http://localhost:8080/"),
                    PaymentServiceProvider.MOCK_PSP
                ),
                DeliveryProvider.MOCK_DELIVERY_PROVIDER,
                emptyList()
            )
            val paymentTransactionId = PaymentTransactionId(UUID.randomUUID().toString())
            val paymentTransaction = PaymentTransaction(
                paymentTransactionId,
                orderAmount,
                Instant.now()
            )
            val dimensions = ParcelDimensions(10.0, 25.0, 30.0, 5.0)
            val parcelId = ParcelId(UUID.randomUUID().toString())


            order.accept()
            order.pay(
                paymentTransaction
            )
            order.beginPacking()
            order.completePacking(parcelId, dimensions)

            then("status is ${OrderStatus.READY}") {
                order.status.shouldBe(OrderStatus.READY)
            }
            then("can be canceled") {
                val result = order.cancel()
                result.isRight().shouldBe(true)
                order.occurredEvents().filterIsInstance<OrderCanceled>().shouldNotBeEmpty()
            }


        }

        given("a sent order") {
            val cartId = CartId(UUID.randomUUID().toString())
            val orderId = OrderId(UUID.randomUUID().toString())
            val paymentId = PaymentId(UUID.randomUUID().toString())
            val orderAmount = BigDecimal.TEN
            val order = Order.create(
                orderId,
                cartId,
                PaymentDetails(
                    paymentId, orderAmount, URL("http://localhost:8080/"),
                    PaymentServiceProvider.MOCK_PSP
                ),
                DeliveryProvider.MOCK_DELIVERY_PROVIDER,
                emptyList()
            )
            val paymentTransactionId = PaymentTransactionId(UUID.randomUUID().toString())
            val paymentTransaction = PaymentTransaction(
                paymentTransactionId,
                orderAmount,
                Instant.now()
            )
            val dimensions = ParcelDimensions(10.0, 25.0, 30.0, 5.0)
            val parcelId = ParcelId(UUID.randomUUID().toString())
            order.accept()
            order.pay(
                paymentTransaction
            )
            order.beginPacking()
            order.completePacking(parcelId, dimensions)
            val deliveryStatus = DeliveryStatus.IN_DELIVERY
            order.changeDeliveryStatus(deliveryStatus)

            then("status is ${OrderStatus.SENT}") {
                order.status.shouldBe(OrderStatus.SENT)
            }

            then("cannot be canceled") {
                val result = order.cancel()
                result.isLeft().shouldBe(true)
                order.occurredEvents().filterIsInstance<OrderCanceled>().shouldBeEmpty()
                result.leftOrNull().shouldBeInstanceOf<CannotCancelSentOrderError>()
            }
        }
    }

    context("Order payment handling") {
        given("an accepted order") {
            val cartId = CartId(UUID.randomUUID().toString())
            val orderId = OrderId(UUID.randomUUID().toString())
            val paymentId = PaymentId(UUID.randomUUID().toString())
            val orderAmount = BigDecimal.TEN
            val order = Order.create(
                orderId,
                cartId,
                PaymentDetails(
                    paymentId, orderAmount, URL("http://localhost:8080/"),
                    PaymentServiceProvider.MOCK_PSP
                ),
                DeliveryProvider.MOCK_DELIVERY_PROVIDER,
                emptyList()
            )

            order.accept()

            `when`("no payment registered") {
                then("its not paid") {
                    order.payment.isPaid.shouldBe(false)
                }
            }

            `when`("its paid with correct amount") {
                val paymentTransactionId = PaymentTransactionId(UUID.randomUUID().toString())
                val paymentTransaction = PaymentTransaction(
                    paymentTransactionId,
                    orderAmount,
                    Instant.now()
                )
                order.pay(
                    paymentTransaction
                )
                then("its paid") {
                    order.payment.isPaid.shouldBe(true)
                }
                then("OrderPaymentReceived event its raised") {
                    order.occurredEvents().filterIsInstance<OrderPaymentReceived>().shouldNotBeEmpty()
                }
                then("OrderPaid event its raised") {
                    order.occurredEvents().filterIsInstance<OrderPaid>().shouldNotBeEmpty()
                }
            }

            `when`("its paid with invalid amount") {
                val paymentTransactionId = PaymentTransactionId(UUID.randomUUID().toString())
                val paymentTransaction = PaymentTransaction(
                    paymentTransactionId,
                    orderAmount.divide(BigDecimal.valueOf(2)),
                    Instant.now()
                )
                order.pay(
                    paymentTransaction
                )
                then("OrderPaymentReceived event its raised") {
                    order.occurredEvents().filterIsInstance<OrderPaymentReceived>().shouldNotBeEmpty()
                }
                then("OrderPaid event its raised") {
                    order.occurredEvents().filterIsInstance<OrderInvalidAmountPaid>().shouldNotBeEmpty()
                }
            }
        }
    }

    context("Order delivery handling") {
        given("a new order") {
            val cartId = CartId(UUID.randomUUID().toString())
            val orderId = OrderId(UUID.randomUUID().toString())
            val paymentId = PaymentId(UUID.randomUUID().toString())
            val orderAmount = BigDecimal.TEN
            val order = Order.create(
                orderId,
                cartId,
                PaymentDetails(
                    paymentId, orderAmount, URL("http://localhost:8080/"),
                    PaymentServiceProvider.MOCK_PSP
                ),
                DeliveryProvider.MOCK_DELIVERY_PROVIDER,
                emptyList()
            )
            val paymentTransactionId = PaymentTransactionId(UUID.randomUUID().toString())
            val paymentTransaction = PaymentTransaction(
                paymentTransactionId,
                orderAmount,
                Instant.now()
            )
            val dimensions = ParcelDimensions(10.0, 25.0, 30.0, 5.0)
            val parcelId = ParcelId(UUID.randomUUID().toString())
            then("Delivery status is ${DeliveryStatus.WAITING}") {
                order.delivery.status.shouldBe(DeliveryStatus.WAITING)
            }
            then("cannot be returned"){
                val result = order.returnOrder()
                result.isLeft().shouldBe(true)
                result.leftOrNull().shouldBeInstanceOf<CannotReturnNotReceivedOrderError>()
            }
            `when`("its in delivery") {
                order.changeDeliveryStatus(DeliveryStatus.IN_DELIVERY)
                then("Order is sent") {
                    order.status.shouldBe(OrderStatus.SENT)
                }
                then("Delivery status is ${DeliveryStatus.IN_DELIVERY}") {
                    order.delivery.status.shouldBe(DeliveryStatus.IN_DELIVERY)
                }
                then("cannot be returned"){
                    val result = order.returnOrder()
                    result.isLeft().shouldBe(true)
                    result.leftOrNull().shouldBeInstanceOf<CannotReturnNotReceivedOrderError>()
                }
            }
            `when`("its delivered") {
                order.changeDeliveryStatus(DeliveryStatus.DELIVERED)
                then("Order is sent") {
                    order.status.shouldBe(OrderStatus.SENT)
                }
                then("Delivery status is ${DeliveryStatus.DELIVERED}") {
                    order.delivery.status.shouldBe(DeliveryStatus.DELIVERED)
                }
                then("can be returned"){
                    val result = order.returnOrder()
                    result.isRight().shouldBe(true)
                }
            }
            `when`("its completed"){
                order.accept()
                order.pay(
                    paymentTransaction
                )
                order.beginPacking()
                order.completePacking(parcelId, dimensions)


                then("delivery have assigned parcel"){
                    order.delivery.parcel.shouldNotBeNull()
                }
            }
        }
    }
})