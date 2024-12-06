package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.order.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.order.domain.error.AppError

class ChangeOrderDeliveryStatusCommandHandler(
    val orderShippingUseCase: OrderShippingUseCase
) : CommandWithResultHandler<ChangeOrderDeliveryStatusCommand, Either<CommandError, Unit>> {
    override suspend fun handle(command: ChangeOrderDeliveryStatusCommand): Either<CommandError, Unit> = either {
        orderShippingUseCase.changeDeliveryStatus(command).bind()
    }
}