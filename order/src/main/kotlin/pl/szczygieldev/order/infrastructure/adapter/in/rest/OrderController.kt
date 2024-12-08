package pl.szczygieldev.order.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercelibrary.command.*
import pl.szczygieldev.order.application.port.`in`.command.*
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.ParcelDimensions
import java.net.URI
import pl.szczygieldev.order.application.port.`in`.query.model.OrderProjection
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.OrderNotFoundError
import pl.szczygieldev.order.infrastructure.adapter.error.CommandNotFoundError
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.presenter.CommandPresenter
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.presenter.OrderPresenter
import java.util.UUID

@RestController
@RequestMapping("/orders")
class OrderController(
    val ordersProjections: OrdersProjections,
    val commandResultStorage: CommandResultStorage,
    val commandPresenter: CommandPresenter,
    val orderPresenter: OrderPresenter,
    val mediator: Mediator
) {
    @GetMapping
    fun getOrders(
        @RequestParam(required = false) orderId: UUID?,
        @RequestParam(required = false) cartId: UUID?
    ): ResponseEntity<*> {
        if (orderId != null) {
            return getOrder(orderId)
        } else if (cartId != null) {
            return getOrderByCartId(cartId)
        }

        return ResponseEntity.ok(
            ordersProjections.findAll().map { orderProjection -> orderPresenter.toDto(orderProjection) })
    }


    private fun getOrder(orderId: UUID): ResponseEntity<*> {
        return either<AppError, OrderProjection> {
            val id = OrderId(orderId.toString())
            ordersProjections.findById(id) ?: raise(OrderNotFoundError.forId(id))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(orderPresenter.toDto(it)) })
    }

    private fun getOrderByCartId(cartId: UUID): ResponseEntity<*> {
        return either<AppError, OrderProjection> {
            val id = CartId(cartId.toString())
            ordersProjections.findByCartId(id) ?: raise(OrderNotFoundError.forCartId(id))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(orderPresenter.toDto(it)) })
    }

    @PutMapping("/{orderId}/accept-commands/{commandId}")
    suspend fun createAcceptCommand(
        @PathVariable orderId: UUID,
        @PathVariable commandId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        return either<AppError, CommandResult> {
            val commandId = CommandId(commandId.toString())
            val command = AcceptOrderCommand(commandId, OrderId(orderId.toString()))
            mediator.sendAsync(command)
            commandResultStorage.findById(commandId) ?: raise(CommandNotFoundError.forId(commandId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            {
                ResponseEntity.created(URI.create("${request.requestURL}/${it.id.id}"))
                    .body(commandPresenter.toDto(it))
            })
    }

    @GetMapping("/{orderId}/accept-commands/{commandId}")
    fun getAcceptCommand(@PathVariable orderId: UUID, @PathVariable commandId: UUID): ResponseEntity<*> {
        val orderId = OrderId(orderId.toString())
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }

    @PutMapping("/{orderId}/reject-commands/{commandId}")
    suspend fun rejectOrder(
        @PathVariable orderId: UUID,
        @PathVariable commandId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        return either<AppError, CommandResult> {
            val commandId = CommandId(commandId.toString())
            mediator.sendAsync(RejectOrderCommand(commandId, OrderId(orderId.toString())))
            commandResultStorage.findById(commandId) ?: raise(CommandNotFoundError.forId(commandId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            {
                ResponseEntity.created(URI.create("${request.requestURL}/${it.id.id}"))
                    .body(commandPresenter.toDto(it))
            })
    }

    @GetMapping("/{orderId}/reject-commands/{commandId}")
    fun getRejectCommand(@PathVariable orderId: UUID, @PathVariable commandId: UUID): ResponseEntity<*> {
        val orderId = OrderId(orderId.toString())
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }

    @PutMapping("/{orderId}/cancel-commands/{commandId}")
    suspend fun cancelOrder(
        @PathVariable orderId: UUID, @PathVariable commandId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        return either<AppError, CommandResult> {
            val commandId = CommandId(commandId.toString())
            mediator.sendAsync(CancelOrderCommand(commandId, OrderId(orderId.toString())))
            commandResultStorage.findById(commandId) ?: raise(CommandNotFoundError.forId(commandId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            {
                ResponseEntity.created(URI.create("${request.requestURL}/${it.id.id}"))
                    .body(commandPresenter.toDto(it))
            })
    }

    @GetMapping("/{orderId}/cancel-commands/{commandId}")
    fun getCancelCommand(@PathVariable orderId: UUID, @PathVariable commandId: UUID): ResponseEntity<*> {
        val orderId = OrderId(orderId.toString())
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }

    @PutMapping("/{orderId}/return-commands/{commandId}")
    suspend fun returnOrder(
        @PathVariable orderId: UUID, @PathVariable commandId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        return either<AppError, CommandResult> {
            val commandId = CommandId(commandId.toString())
            mediator.sendAsync(ReturnOrderCommand(commandId, OrderId(orderId.toString())))
            commandResultStorage.findById(commandId) ?: raise(CommandNotFoundError.forId(commandId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            {
                ResponseEntity.created(URI.create("${request.requestURL}/${it.id.id}"))
                    .body(commandPresenter.toDto(it))
            })
    }

    @GetMapping("/{orderId}/return-commands/{commandId}")
    fun getReturnCommand(@PathVariable orderId: UUID, @PathVariable commandId: UUID): ResponseEntity<*> {
        val orderId = OrderId(orderId.toString())
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }


    @PutMapping("/{orderId}/beginPacking-commands/{commandId}")
    suspend fun beginPackingOrder(
        @PathVariable orderId: UUID, @PathVariable commandId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        return either<AppError, CommandResult> {
            val command = BeginOrderPackingCommand( CommandId(commandId.toString()),OrderId(orderId.toString()))
            val commandId = command.id
            mediator.send(command).bind()
            commandResultStorage.findById(commandId) ?: raise(CommandNotFoundError.forId(commandId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            {
                ResponseEntity.created(URI.create("${request.requestURL}/${it.id.id}"))
                    .body(commandPresenter.toDto(it))
            })
    }

    @GetMapping("/{orderId}/beginPacking-commands/{commandId}")
    fun getBeginPackingOrderCommand(@PathVariable orderId: UUID, @PathVariable commandId: UUID): ResponseEntity<*> {
        val orderId = OrderId(orderId.toString())
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }

    @PutMapping("/{orderId}/completePacking-commands/{commandId}")
    suspend fun completePackingOrder(
        @PathVariable orderId: UUID, @PathVariable commandId: UUID,
        @RequestBody parcelDimensions: ParcelDimensions,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        return either<AppError, CommandResult> {
            val command = CompleteOrderPackingCommand(
                CommandId(commandId.toString()),
                OrderId(orderId.toString()),
                parcelDimensions
            )
            val commandId = command.id
            mediator.send(
                command
            ).bind()
            commandResultStorage.findById(commandId) ?: raise(CommandNotFoundError.forId(commandId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            {
                ResponseEntity.created(URI.create("${request.requestURL}/${it.id.id}"))
                    .body(commandPresenter.toDto(it))
            })
    }

    @GetMapping("/{orderId}/completePacking-commands/{commandId}")
    fun getCompletePackingOrderCommand(
        @PathVariable orderId: UUID,
        @PathVariable commandId: UUID
    ): ResponseEntity<*> {
        val orderId = OrderId(orderId.toString())
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }

    private fun getCommandResultResponse(orderId: OrderId, commandId: CommandId): ResponseEntity<*> {
        return either {
            ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
            commandResultStorage.findById(commandId) ?: raise(CommandNotFoundError.forId(commandId))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(commandPresenter.toDto(it)) })
    }
}