package pl.szczygieldev.client.infrastructure.adapter.`in`.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RequestMapping("/clients")
@RestController
class ClientController {

    @GetMapping("/current")
    fun getCurrentClient(principal: Principal): ResponseEntity<*>{
        return ResponseEntity.ok().body("OK")
    }
}