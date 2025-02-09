package pl.szczygieldev.order.infrastructure.adapter.out.persistence

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import pl.szczygieldev.order.application.port.`in`.query.model.CartProjection
import pl.szczygieldev.order.application.port.out.CartsProjections
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.CartStatus
import pl.szczygieldev.order.domain.ProductId
import pl.szczygieldev.order.domain.UserId
import pl.szczygieldev.order.infrastructure.adapter.out.persistence.table.CartProjectionEntryTable
import pl.szczygieldev.order.infrastructure.adapter.out.persistence.table.CartProjectionTable

@Repository
internal class CartProjectionRepository : CartsProjections {

    override fun findById(id: CartId): CartProjection? = transaction {
        val cartProjectionEntity = CartProjectionTable
            .selectAll().where(CartProjectionTable.id.eq(id.idAsUUID())).singleOrNull() ?: return@transaction null
        val cartItemEntities =
            CartProjectionEntryTable.selectAll().where(CartProjectionEntryTable.cartId.eq(id.idAsUUID()))

        val cartProjection = CartProjection(
            CartId(cartProjectionEntity[CartProjectionTable.id]),
            cartProjectionEntity[CartProjectionTable.status],
            cartProjectionEntity[CartProjectionTable.amount],
            cartItemEntities.map { cartItem ->
                CartProjection.Entry(
                    ProductId(cartItem[CartProjectionEntryTable.productId]),
                    cartItem[CartProjectionEntryTable.quantity]
                )
            }
        )

        return@transaction cartProjection
    }

    override fun save(cart: CartProjection) = transaction {
        CartProjectionTable.upsert {
            it[id] = cart.cartId.id
            it[status] = cart.status
            it[amount] = cart.amount
        }

        CartProjectionEntryTable.deleteWhere(
            op = {
                cartId.eq(cart.cartId.id)
            })

        cart.items.forEach { cartEntry ->
            CartProjectionEntryTable.insert {
                it[cartId] = cart.cartId.id
                it[productId] = cartEntry.productId.id
                it[quantity] = cartEntry.quantity
            }
        }
    }



    //TODO - replace when implementing users
    override fun findActiveForUser(id: UserId): CartProjection? =
        transaction {
            val cartProjectionEntity = CartProjectionTable
                .selectAll().where(CartProjectionTable.status.eq(CartStatus.ACTIVE)).singleOrNull()
                ?: return@transaction null
            val cartItemEntities = CartProjectionEntryTable.selectAll()
                .where(CartProjectionEntryTable.cartId.eq(cartProjectionEntity[CartProjectionTable.id]))

            val cartProjection = CartProjection(
                CartId(cartProjectionEntity[CartProjectionTable.id]),
                cartProjectionEntity[CartProjectionTable.status],
                cartProjectionEntity[CartProjectionTable.amount],
                cartItemEntities.map { cartItem ->
                    CartProjection.Entry(
                        ProductId(cartItem[CartProjectionEntryTable.productId]),
                        cartItem[CartProjectionEntryTable.quantity]
                    )
                }
            )

            return@transaction cartProjection
        }

}