package pl.szczygieldev.order.infrastructure.adapter.out.persistence

import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import pl.szczygieldev.order.application.port.`in`.query.model.OrderProjection
import pl.szczygieldev.order.application.port.`in`.query.model.PaymentProjection
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.domain.*
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.ParcelId
import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.infrastructure.adapter.out.persistence.table.OrderProjectionEntryTable
import pl.szczygieldev.order.infrastructure.adapter.out.persistence.table.OrderProjectionPaymentTable
import pl.szczygieldev.order.infrastructure.adapter.out.persistence.table.OrderProjectionPaymentTransactionTable
import pl.szczygieldev.order.infrastructure.adapter.out.persistence.table.OrderProjectionPaymentTransactionTable.datetime
import pl.szczygieldev.order.infrastructure.adapter.out.persistence.table.OrderProjectionTable
import java.net.URL

@Repository
internal class OrderProjectionRepository : OrdersProjections {

    override fun findById(id: OrderId): OrderProjection? = transaction {
        val found = OrderProjectionTable.join(
            OrderProjectionPaymentTable,
            JoinType.INNER,
            OrderProjectionTable.paymentId,
            OrderProjectionPaymentTable.id
        ).selectAll().where { OrderProjectionTable.id.eq(id.idAsUUID()) }.singleOrNull()
            ?: return@transaction null

        return@transaction mapToProjection(found)
    }

    override fun save(order: OrderProjection) = transaction {
        val payment = order.paymentProjection
        val delivery = order.delivery

        OrderProjectionPaymentTable.upsert {
            it[id] = payment.paymentId.idAsUUID()
            it[amount] = payment.amount
            it[amountPaid] = payment.amountPaid
            it[paymentServiceProvider] = payment.paymentServiceProvider
            it[status] = payment.status
            it[url] = payment.paymentURL.toString()
        }

        payment.transactions.forEach { transaction ->
            OrderProjectionPaymentTransactionTable.upsert {
                it[id] = transaction.id.idAsUUID()
                it[paymentId] = payment.paymentId.idAsUUID()
                it[amount] = transaction.amount
                it[datetime] = transaction.timestamp.toKotlinInstant()
            }
        }

        OrderProjectionTable.upsert {
            it[id] = order.orderId.idAsUUID()
            it[cartId] = order.cartId.idAsUUID()
            it[status] = order.status
            it[paymentId] = payment.paymentId.idAsUUID()
            it[deliveryProvider] = delivery.deliveryProvider
            it[deliveryStatus] = delivery.status
            it[parcelId] = delivery.parcel?.parcelId?.id
            it[width] = delivery.parcel?.parcelDimensions?.width
            it[length] = delivery.parcel?.parcelDimensions?.length
            it[height] = delivery.parcel?.parcelDimensions?.height
            it[weight] = delivery.parcel?.parcelDimensions?.weight
            it[createdAt] = order.createdAt.toKotlinInstant()
        }

        OrderProjectionEntryTable.deleteWhere { orderId.eq(order.orderId.idAsUUID()) }

        order.items.forEach { item ->
            OrderProjectionEntryTable.insert {
                it[orderId] = order.orderId.idAsUUID()
                it[productId] = item.productId.idAsUUID()
                it[title] = item.title
                it[price] = item.price
                it[quantity] = item.quantity
                it[imageId] = item.imageId.idAsUUID()
            }
        }
    }

    override fun findAll(): List<OrderProjection> = transaction {
        val projections = OrderProjectionTable.join(
            OrderProjectionPaymentTable,
            JoinType.INNER,
            OrderProjectionTable.paymentId,
            OrderProjectionPaymentTable.id
        ).selectAll().map { row ->
            return@map mapToProjection(row)
        }

        return@transaction projections
    }

    override fun findByParcelIdentifier(identifier: ParcelId): OrderProjection? =
        transaction {
            val found = OrderProjectionTable.join(
                OrderProjectionPaymentTable,
                JoinType.INNER,
                OrderProjectionTable.paymentId,
                OrderProjectionPaymentTable.id
            ).selectAll().where { OrderProjectionTable.parcelId.eq(identifier.idAsUUID()) }.singleOrNull()
                ?: return@transaction null

            return@transaction mapToProjection(found)
        }

    override fun findByPaymentId(paymentId: PaymentId): OrderProjection? = transaction {
        val found = OrderProjectionTable.join(
            OrderProjectionPaymentTable,
            JoinType.INNER,
            OrderProjectionTable.paymentId,
            OrderProjectionPaymentTable.id
        ).selectAll().where { OrderProjectionTable.paymentId.eq(paymentId.idAsUUID()) }.singleOrNull()
            ?: return@transaction null

        return@transaction mapToProjection(found)
    }


    override fun findByCartId(cartId: CartId): OrderProjection? =
        transaction {
            val found = OrderProjectionTable.join(
                OrderProjectionPaymentTable,
                JoinType.INNER,
                OrderProjectionTable.paymentId,
                OrderProjectionPaymentTable.id
            ).selectAll().where { OrderProjectionTable.cartId.eq(cartId.idAsUUID()) }.singleOrNull()
                ?: return@transaction null

            return@transaction  mapToProjection(found)
        }

    override fun findPage(offset: Int, limit: Int): List<OrderProjection> = transaction {
        val projections = OrderProjectionTable.join(
            OrderProjectionPaymentTable,
            JoinType.INNER,
            OrderProjectionTable.paymentId,
            OrderProjectionPaymentTable.id
        ).selectAll().offset(offset.toLong()).limit(limit).map { row ->
            return@map mapToProjection(row)
        }

        return@transaction projections
    }

    private fun mapToProjection(row: ResultRow): OrderProjection {
        val paymentTransactions = OrderProjectionPaymentTransactionTable.selectAll()
            .where(OrderProjectionPaymentTransactionTable.paymentId.eq(row[OrderProjectionPaymentTable.id]))
            .map { paymentTransaction ->

                return@map PaymentTransaction(
                    PaymentTransactionId(paymentTransaction[OrderProjectionPaymentTransactionTable.id]),
                    paymentTransaction[OrderProjectionPaymentTransactionTable.amount],
                    paymentTransaction[datetime].toJavaInstant(),
                )
            }

        val payment = PaymentProjection(
            PaymentId(row[OrderProjectionPaymentTable.id]),
            row[OrderProjectionPaymentTable.amount],
            row[OrderProjectionPaymentTable.amountPaid],
            row[OrderProjectionPaymentTable.paymentServiceProvider],
            row[OrderProjectionPaymentTable.status],
            URL(row[OrderProjectionPaymentTable.url]),
            paymentTransactions
        )

        val orderItems = OrderProjectionEntryTable.selectAll()
            .where(OrderProjectionEntryTable.id.eq(row[OrderProjectionTable.id])).map { orderItem ->
                return@map OrderProjection.OrderItemProjection(
                    ProductId(orderItem[OrderProjectionEntryTable.productId]),
                    orderItem[OrderProjectionEntryTable.title],
                    orderItem[OrderProjectionEntryTable.price],
                    orderItem[OrderProjectionEntryTable.quantity],
                    ImageId(orderItem[OrderProjectionEntryTable.imageId]),
                )
            }

        var parcel: Parcel? = null

        if (row[OrderProjectionTable.parcelId] != null) {
            parcel = Parcel(
                ParcelId(row[OrderProjectionTable.parcelId]!!),
                ParcelDimensions(
                    row[OrderProjectionTable.width]!!,
                    row[OrderProjectionTable.length]!!,
                    row[OrderProjectionTable.height]!!,
                    row[OrderProjectionTable.weight]!!
                )
            )
        }

        return OrderProjection(
            OrderId(row[OrderProjectionTable.id]),
            CartId(row[OrderProjectionTable.cartId]),
            row[OrderProjectionTable.status],
            payment,
            Delivery(
                row[OrderProjectionTable.deliveryProvider],
                row[OrderProjectionTable.deliveryStatus],
                parcel
            ),
            row[OrderProjectionTable.createdAt].toJavaInstant(), orderItems
        )
    }
}