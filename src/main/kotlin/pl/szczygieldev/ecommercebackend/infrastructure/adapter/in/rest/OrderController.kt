package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AcceptOrderCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CancelOrderCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.RejectOrderCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ReturnOrderCommand
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence.OrderRepository

@RestController
@RequestMapping("/orders")
class OrderController(val orderUseCase: OrderUseCase, val orderRepository: OrderRepository) {

    /*
    @GetMapping
    fun getOrders(): ResponseEntity<*> {
        return ResponseEntity.ok(orderRepository.findAll())
    }
    */
    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: String): ResponseEntity<*> {
        return ResponseEntity.ok(orderRepository.findById(OrderId(orderId)))
    }
    //Change to command pattern
    @PostMapping("/{orderId}/accept")
    fun acceptOrder(@PathVariable orderId: String) : ResponseEntity<*>{
        return ResponseEntity.ok(  orderUseCase.acceptOrder(AcceptOrderCommand(OrderId(orderId))))
    }

    @PostMapping("/{orderId}/reject")
    fun rejectOrder(@PathVariable orderId: String) : ResponseEntity<*>{
        return ResponseEntity.ok(   orderUseCase.rejectOrder(RejectOrderCommand(OrderId(orderId))))
    }

    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: String) : ResponseEntity<*>{
        return ResponseEntity.ok(  orderUseCase.cancelOrder(CancelOrderCommand(OrderId(orderId))))
    }

    @PostMapping("/{orderId}/return")
    fun returnOrder(@PathVariable orderId: String) : ResponseEntity<*>{
        return ResponseEntity.ok( orderUseCase.returnOrder(ReturnOrderCommand(OrderId(orderId))))
    }
}