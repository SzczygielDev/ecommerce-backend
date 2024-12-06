package pl.szczygieldev.order.infrastructure.adapter.`in`.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercelibrary.command.CommandResultStorage
import pl.szczygieldev.order.infrastructure.adapter.`in`.rest.presenter.CommandPresenter

@RequestMapping("/development")
@RestController
class DevelopmentController(
    val commandResultStorage: CommandResultStorage,
    val commandPresenter: CommandPresenter,
) {
    @GetMapping("/commands")
    fun getCommands(): ResponseEntity<*> {
        return ResponseEntity.ok(commandResultStorage.findAll().map { commandPresenter.toFullDto(it) })
    }
}