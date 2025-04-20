package pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.order.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.order.application.port.`in`.command.ChangeOrderDeliveryStatusCommand

internal class ChangeOrderDeliveryStatusCommandHandler(
    val orderShippingUseCase: OrderShippingUseCase
) : CommandWithResultHandler<ChangeOrderDeliveryStatusCommand, Either<ChangeOrderDeliveryStatusCommand.Error, Unit>> {
    override suspend fun handle(command: ChangeOrderDeliveryStatusCommand): Either<ChangeOrderDeliveryStatusCommand.Error, Unit> = either {
        orderShippingUseCase.changeDeliveryStatus(command).bind()
    }
}