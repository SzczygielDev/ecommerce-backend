package pl.szczygieldev.ecommercebackend.application.port.`in`.command.common

import arrow.core.Either
import com.trendyol.kediatr.CommandWithResult
import pl.szczygieldev.ecommercebackend.domain.error.AppError


//TODO - for now its only external dependency in application, in future should be removed
abstract class Command(val id: CommandId = CommandId()) : CommandWithResult<Either<AppError, Unit>>