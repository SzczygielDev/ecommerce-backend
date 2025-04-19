package pl.szczygieldev.cart.application.port.`in`.command

import pl.szczygieldev.cart.domain.ClientId
import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError

internal class CreateCartCommand(val clientId: ClientId) : Command<CreateCartCommand.Error>(){
    sealed class Error(override val message: String, override val code: String) :CommandError(message,code)
}