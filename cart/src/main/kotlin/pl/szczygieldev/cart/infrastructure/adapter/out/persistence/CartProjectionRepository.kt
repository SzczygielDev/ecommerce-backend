package pl.szczygieldev.cart.infrastructure.adapter.out.persistence

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import pl.szczygieldev.cart.CartProjection
import pl.szczygieldev.cart.application.port.out.CartsProjections
import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.cart.domain.CartStatus
import pl.szczygieldev.cart.domain.ProductId
import pl.szczygieldev.cart.domain.UserId
import pl.szczygieldev.cart.infrastructure.adapter.out.persistence.table.CartProjectionEntryTable
import pl.szczygieldev.cart.infrastructure.adapter.out.persistence.table.CartProjectionTable

@Repository("cartModule.CartProjectionRepository")
internal class CartProjectionRepository : CartsProjections {

    override fun findById(id: CartId): CartProjection? = transaction {
        val cartProjectionEntity = CartProjectionTable
            .selectAll().where(CartProjectionTable.id.eq(id.idAsUUID())).singleOrNull() ?: return@transaction null
        val cartItemEntities =
            CartProjectionEntryTable.selectAll().where(CartProjectionEntryTable.cartId.eq(id.idAsUUID()))

        val cartProjection = CartProjection(
            cartProjectionEntity[CartProjectionTable.id],
            cartProjectionEntity[CartProjectionTable.status].toString(),
            cartProjectionEntity[CartProjectionTable.amount],
            cartItemEntities.map { cartItem ->
                CartProjection.Entry(
                    cartItem[CartProjectionEntryTable.productId],
                    cartItem[CartProjectionEntryTable.quantity]
                )
            }
        )

        return@transaction cartProjection
    }

    override fun save(cart: CartProjection) = transaction {
        CartProjectionTable.upsert {
            it[id] = cart.cartId
            it[status] = CartStatus.valueOf(cart.status)
            it[amount] = cart.amount
        }

        CartProjectionEntryTable.deleteWhere(
            op = {
                cartId.eq(cart.cartId)
            })

        cart.items.forEach { cartEntry ->
            CartProjectionEntryTable.insert {
                it[cartId] = cart.cartId
                it[productId] = cartEntry.productId
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
                cartProjectionEntity[CartProjectionTable.id],
                cartProjectionEntity[CartProjectionTable.status].toString(),
                cartProjectionEntity[CartProjectionTable.amount],
                cartItemEntities.map { cartItem ->
                    CartProjection.Entry(
                        cartItem[CartProjectionEntryTable.productId],
                        cartItem[CartProjectionEntryTable.quantity]
                    )
                }
            )

            return@transaction cartProjection
        }

}