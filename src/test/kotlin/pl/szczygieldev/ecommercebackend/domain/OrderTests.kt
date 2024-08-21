package pl.szczygieldev.ecommercebackend.domain

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import pl.szczygieldev.ecommercebackend.domain.error.AlreadyAcceptedOrderError
import pl.szczygieldev.ecommercebackend.domain.error.CannotPackageNotAcceptedOrderError
import pl.szczygieldev.ecommercebackend.domain.error.NotPaidOrderError
import pl.szczygieldev.ecommercebackend.domain.event.*
import java.math.BigDecimal
import java.net.URL
import java.time.Instant
import java.util.*

class OrderTests : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

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
        then("packing process cannot start") {
            val result = order.beginPacking()
            result.isLeft().shouldBe(true)
            result.leftOrNull().shouldBeInstanceOf<CannotPackageNotAcceptedOrderError>()
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
        `when`("its send") {
            order.changeDeliveryStatus(DeliveryStatus.IN_DELIVERY)
            then("cannot be canceled") {
                val result = order.cancel()
                result.isLeft().shouldBe(true)
                order.occurredEvents().filterIsInstance<OrderCanceled>().shouldBeEmpty()
            }
        }

    }
})