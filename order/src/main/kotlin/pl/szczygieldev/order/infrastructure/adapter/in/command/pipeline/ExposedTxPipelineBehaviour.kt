package pl.szczygieldev.order.infrastructure.adapter.`in`.command.pipeline

import arrow.core.Either
import com.trendyol.kediatr.PipelineBehavior
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component

@Component
class ExposedTxPipelineBehaviour : PipelineBehavior {
    override suspend fun <TRequest, TResponse> handle(
        request: TRequest,
        next: suspend (TRequest) -> TResponse
    ): TResponse {
        val result = newSuspendedTransaction {
            val response = next(request)
            if (response is Either<*, *>) {
                response.fold({
                    rollback()
                },{
                    commit()
                })

            }

            return@newSuspendedTransaction response
        }
        return result
    }
}