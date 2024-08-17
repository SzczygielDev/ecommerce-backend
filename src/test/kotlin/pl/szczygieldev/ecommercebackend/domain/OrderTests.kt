package pl.szczygieldev.ecommercebackend.domain

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
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

    given("an order") {
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
        then("packing process cannot start") {
            val result = order.beginPacking()
            result.isLeft().shouldBe(true)
            result.leftOrNull().shouldBeInstanceOf<CannotPackageNotAcceptedOrderError>()
        }

        `when`("its accepted") {
            order.accept()
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
            and("its paid") {
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
            and("its not paid") {
                then("packing process cannot start") {
                    val result = order.beginPacking()
                    result.isLeft().shouldBe(true)
                    result.leftOrNull().shouldBeInstanceOf<NotPaidOrderError>()
                }
            }
        }

        `when`("packing process started") {
            order.accept()
            order.pay(
                paymentTransaction
            )
            order.beginPacking()

            then("packing process can be completed") {
                val result = order.completePacking(parcelId, dimensions)

                result.isRight().shouldBe(true)
                order.occurredEvents().filterIsInstance<OrderPackaged>().shouldNotBeEmpty()
            }
        }
    }
})