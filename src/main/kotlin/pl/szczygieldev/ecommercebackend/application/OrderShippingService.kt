package pl.szczygieldev.ecommercebackend.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.handlers.BeginOrderPackingCommandHandler
import pl.szczygieldev.ecommercebackend.application.handlers.ChangeOrderDeliveryStatusCommandHandler
import pl.szczygieldev.ecommercebackend.application.handlers.CompleteOrderPackingCommandHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CompleteOrderPackingCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.shared.architecture.UseCase

@UseCase
class OrderShippingService(
    val beginOrderPackingCommandHandler: BeginOrderPackingCommandHandler,
    val completeOrderPackingCommandHandler: CompleteOrderPackingCommandHandler,
    val changeOrderDeliveryStatusCommandHandler: ChangeOrderDeliveryStatusCommandHandler
) : OrderShippingUseCase {
    override suspend fun beginPacking(command: BeginOrderPackingCommand): Either<AppError, Unit> = either {
        beginOrderPackingCommandHandler.execute(command).bind()
    }

    override suspend fun completePacking(command: CompleteOrderPackingCommand): Either<AppError, Unit> = either {
        completeOrderPackingCommandHandler.execute(command).bind()
    }


    override suspend fun changeDeliveryStatus(command: ChangeOrderDeliveryStatusCommand): Either<AppError, Unit> = either {
        changeOrderDeliveryStatusCommandHandler.execute(command).bind()
    }
}