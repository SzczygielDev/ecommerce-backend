package pl.szczygieldev.order.infrastructure.adapter.`in`.api

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
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
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.error.CommandNotFoundError
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.presenter.CommandPresenter
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.presenter.OrderPresenter
import java.util.UUID

@RestController
@RequestMapping("/orders")
internal class OrderController(
    val ordersProjections: OrdersProjections,
    val commandQueue: CommandQueue,
    val commandPresenter: CommandPresenter,
    val orderPresenter: OrderPresenter,
    val mediator: Mediator
) {
    @GetMapping
    fun getOrders(
        @RequestParam(required = false) orderId: UUID?,
        @RequestParam(required = false) cartId: UUID?,
        @RequestParam(required = false) offset: Int?,
        @RequestParam(required = false) limit: Int?,
    ): ResponseEntity<*> {
        if (orderId != null) {
            return getOrder(orderId)
        } else if (cartId != null) {
            return getOrderByCartId(cartId)
        }
        if (offset != null && limit != null) {
            return ResponseEntity.ok(
                ordersProjections.findPage(offset, limit)
                    .map { orderProjection -> orderPresenter.toDto(orderProjection) })
        }
        return ResponseEntity.ok(
            ordersProjections.findAll().map { orderProjection -> orderPresenter.toDto(orderProjection) })
    }


    private fun getOrder(orderId: UUID): ResponseEntity<*> {
        val id = OrderId(orderId)
        val order = ordersProjections.findById(id) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Cannot find order with id='${id.id()}'.")
            )

        return ResponseEntity.ok(orderPresenter.toDto(order))
    }

    private fun getOrderByCartId(cartId: UUID): ResponseEntity<*> {
        val id = CartId(cartId)

        val order = ordersProjections.findByCartId(id) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Cannot find order with cart id='${cartId}'.")
            )

        return ResponseEntity.ok(orderPresenter.toDto(order))
    }

    @PutMapping("/{orderId}/accept-commands/{commandId}")
    suspend fun createAcceptCommand(
        @PathVariable orderId: UUID,
        @PathVariable commandId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val commandId = CommandId(commandId.toString())
        val command = AcceptOrderCommand(commandId, OrderId(orderId))
        commandQueue.push(command)
        val status = commandQueue.getCommandStatus(commandId) ?: return CommandNotFoundError.forId(commandId)

        return ResponseEntity.created(URI.create("${request.requestURL}/${status.id.id}"))
            .body(commandPresenter.toDto(status))
    }

    @GetMapping("/{orderId}/accept-commands/{commandId}")
    fun getAcceptCommand(@PathVariable orderId: UUID, @PathVariable commandId: UUID): ResponseEntity<*> {
        val orderId = OrderId(orderId)
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }

    @PutMapping("/{orderId}/reject-commands/{commandId}")
    suspend fun rejectOrder(
        @PathVariable orderId: UUID,
        @PathVariable commandId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val commandId = CommandId(commandId.toString())
        commandQueue.push(RejectOrderCommand(commandId, OrderId(orderId)))
        val status = commandQueue.getCommandStatus(commandId) ?: return CommandNotFoundError.forId(commandId)

        return ResponseEntity.created(URI.create("${request.requestURL}/${status.id.id}"))
            .body(commandPresenter.toDto(status))
    }

    @GetMapping("/{orderId}/reject-commands/{commandId}")
    fun getRejectCommand(@PathVariable orderId: UUID, @PathVariable commandId: UUID): ResponseEntity<*> {
        val orderId = OrderId(orderId)
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }

    @PutMapping("/{orderId}/cancel-commands/{commandId}")
    suspend fun cancelOrder(
        @PathVariable orderId: UUID, @PathVariable commandId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val commandId = CommandId(commandId.toString())
        commandQueue.push(CancelOrderCommand(commandId, OrderId(orderId)))
        val status = commandQueue.getCommandStatus(commandId) ?: return CommandNotFoundError.forId(commandId)

        return ResponseEntity.created(URI.create("${request.requestURL}/${status.id.id}"))
            .body(commandPresenter.toDto(status))
    }

    @GetMapping("/{orderId}/cancel-commands/{commandId}")
    fun getCancelCommand(@PathVariable orderId: UUID, @PathVariable commandId: UUID): ResponseEntity<*> {
        val orderId = OrderId(orderId)
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }

    @PutMapping("/{orderId}/return-commands/{commandId}")
    suspend fun returnOrder(
        @PathVariable orderId: UUID, @PathVariable commandId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val commandId = CommandId(commandId.toString())
        commandQueue.push(ReturnOrderCommand(commandId, OrderId(orderId)))
        val status = commandQueue.getCommandStatus(commandId) ?: return CommandNotFoundError.forId(commandId)

        return ResponseEntity.created(URI.create("${request.requestURL}/${status.id.id}"))
            .body(commandPresenter.toDto(status))
    }

    @GetMapping("/{orderId}/return-commands/{commandId}")
    fun getReturnCommand(@PathVariable orderId: UUID, @PathVariable commandId: UUID): ResponseEntity<*> {
        val orderId = OrderId(orderId)
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }


    @PutMapping("/{orderId}/beginPacking-commands/{commandId}")
    suspend fun beginPackingOrder(
        @PathVariable orderId: UUID, @PathVariable commandId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val command = BeginOrderPackingCommand(CommandId(commandId.toString()), OrderId(orderId))
        val commandId = command.id
        commandQueue.push(command)
        val status = commandQueue.getCommandStatus(commandId) ?: return CommandNotFoundError.forId(commandId)

        return ResponseEntity.created(URI.create("${request.requestURL}/${status.id.id}"))
            .body(commandPresenter.toDto(status))
    }

    @GetMapping("/{orderId}/beginPacking-commands/{commandId}")
    fun getBeginPackingOrderCommand(@PathVariable orderId: UUID, @PathVariable commandId: UUID): ResponseEntity<*> {
        val orderId = OrderId(orderId)
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }

    @PutMapping("/{orderId}/completePacking-commands/{commandId}")
    suspend fun completePackingOrder(
        @PathVariable orderId: UUID, @PathVariable commandId: UUID,
        @RequestBody parcelDimensions: ParcelDimensions,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val command = CompleteOrderPackingCommand(
            CommandId(commandId.toString()),
            OrderId(orderId),
            parcelDimensions
        )
        val commandId = command.id
        commandQueue.push(command)
        val status = commandQueue.getCommandStatus(commandId) ?: return CommandNotFoundError.forId(commandId)

        return ResponseEntity.created(URI.create("${request.requestURL}/${status.id.id}"))
            .body(commandPresenter.toDto(status))
    }

    @GetMapping("/{orderId}/completePacking-commands/{commandId}")
    fun getCompletePackingOrderCommand(
        @PathVariable orderId: UUID,
        @PathVariable commandId: UUID
    ): ResponseEntity<*> {
        val orderId = OrderId(orderId)
        val commandId = CommandId(commandId.toString())
        return getCommandResultResponse(orderId, commandId)
    }

    private fun getCommandResultResponse(orderId: OrderId, commandId: CommandId): ResponseEntity<*> {
        ordersProjections.findById(orderId) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Cannot find order with id='${orderId.id()}'.")
            )
        val status = commandQueue.getCommandStatus(commandId) ?: return CommandNotFoundError.forId(commandId)

        return ResponseEntity.ok(commandPresenter.toDto(status))
    }
}