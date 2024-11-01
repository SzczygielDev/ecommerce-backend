package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class ChangeOrderDeliveryStatusCommandHandler(
    val orderShippingUseCase: OrderShippingUseCase
) : CommandWithResultHandler<ChangeOrderDeliveryStatusCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: ChangeOrderDeliveryStatusCommand): Either<AppError, Unit> = either {
        orderShippingUseCase.changeDeliveryStatus(command).bind()
    }
}