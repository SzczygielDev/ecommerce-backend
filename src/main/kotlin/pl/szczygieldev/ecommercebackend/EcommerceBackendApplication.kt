package pl.szczygieldev.ecommercebackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EcommerceBackendApplication

fun main(args: Array<String>) {
	System.setProperty("spring.devtools.restart.enabled", "false");
	runApplication<EcommerceBackendApplication>(*args)
}
