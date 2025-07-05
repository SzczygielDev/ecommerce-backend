package pl.szczygieldev.client

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
@Component("clientModule.Warmup")
internal class Warmup() {
    @EventListener(ApplicationReadyEvent::class)
    suspend fun initData() {

    }
}