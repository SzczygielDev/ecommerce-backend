package pl.szczygieldev.order.infrastructure.adapter.`in`.api.error

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import pl.szczygieldev.ecommercelibrary.command.CommandId

internal abstract class CommandNotFoundError(val message: String){
    companion object {
        fun forId(id: CommandId): ResponseEntity<*> {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Cannot find command with id='${id.id}'."))
        }
    }
}
