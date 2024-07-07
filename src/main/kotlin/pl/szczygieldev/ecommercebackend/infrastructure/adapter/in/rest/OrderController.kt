package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.*
import pl.szczygieldev.ecommercebackend.application.port.out.CommandStorage
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.ParcelDimensions
import pl.szczygieldev.shared.architecture.CommandId
import java.net.URI
import kotlinx.coroutines.*
import pl.szczygieldev.ecommercebackend.application.model.OrderProjection
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.error.CommandNotFoundError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter.CommandPresenter

@RestController
@RequestMapping("/orders")
class OrderController(
    val orderUseCase: OrderUseCase,
    val ordersProjections: OrdersProjections,
    val orderShippingUseCase: OrderShippingUseCase,
    val commandStorage: CommandStorage,
    val commandPresenter: CommandPresenter
) {
    val coroutineScope = CoroutineScope(SupervisorJob())
    @GetMapping
    fun getOrders(): ResponseEntity<*> {
        return ResponseEntity.ok(ordersProjections.findAll())
    }

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: String): ResponseEntity<*> {
        return either<AppError, OrderProjection> {
            val id = OrderId(orderId)
            ordersProjections.findById(id) ?: raise(OrderNotFoundError.forId(id))
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(it) })
    }

    @GetMapping("/{orderId}/accept-commands/{commandId}")
    fun getAcceptCommand(@PathVariable orderId: String, @PathVariable commandId: String): ResponseEntity<*> {
        return either {
            val orderId = OrderId(orderId)
            ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))

            val commandId = CommandId(commandId)
            commandStorage.findById(commandId) ?: raise(CommandNotFoundError.forId(commandId))

        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(commandPresenter.toDto(it)) })
    }

    @PutMapping("/{orderId}/accept-commands")
    fun createAcceptCommand(@PathVariable orderId: String, request: HttpServletRequest): ResponseEntity<*> {
        val command = AcceptOrderCommand(OrderId(orderId))
        commandStorage.runCommand(command)

        coroutineScope.launch {
            orderUseCase.acceptOrder(command).fold({ error ->
                commandStorage.commandFailed(command.id, error)
            }, {
                commandStorage.commandSuccess(command.id)
            })
        }

        return ResponseEntity.created(URI.create("${request.requestURL}/${command.id.id}"))
            .body(commandStorage.findById(command.id)!!)
    }


    @PutMapping("/{orderId}/reject-commands")
    fun rejectOrder(@PathVariable orderId: String): ResponseEntity<*> {
        return ResponseEntity.ok(orderUseCase.rejectOrder(RejectOrderCommand(OrderId(orderId))))
    }

    @PutMapping("/{orderId}/cancel-commands")
    fun cancelOrder(@PathVariable orderId: String): ResponseEntity<*> {
        return ResponseEntity.ok(orderUseCase.cancelOrder(CancelOrderCommand(OrderId(orderId))))
    }

    @PutMapping("/{orderId}/return-commands")
    fun returnOrder(@PathVariable orderId: String): ResponseEntity<*> {
        return ResponseEntity.ok(orderUseCase.returnOrder(ReturnOrderCommand(OrderId(orderId))))
    }


    @PutMapping("/{orderId}/beginPacking-commands")
    fun beginPackingOrder(@PathVariable orderId: String): ResponseEntity<*> {
        return ResponseEntity.ok(orderShippingUseCase.beginPacking(BeginOrderPackingCommand(OrderId(orderId))))
    }

    @PutMapping("/{orderId}/completePacking-commands")
    fun completePackingOrder(
        @PathVariable orderId: String,
        @RequestBody parcelDimensions: ParcelDimensions
    ): ResponseEntity<*> {
        return ResponseEntity.ok(
            orderShippingUseCase.completePacking(
                CompleteOrderPackingCommand(
                    OrderId(orderId),
                    parcelDimensions
                )
            )
        )
    }
}