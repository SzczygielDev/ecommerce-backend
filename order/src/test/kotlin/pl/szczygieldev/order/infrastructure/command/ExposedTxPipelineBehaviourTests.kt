package pl.szczygieldev.order.infrastructure.command

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import com.trendyol.kediatr.Mediator
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.InfrastructureError

@SpringBootTest
class ExposedTxPipelineBehaviourTests {
    object TxTestTable : IntIdTable() {
        val name = varchar("someColumn", 50).uniqueIndex()
    }

    class SuccessCommand : Command()

    class FailingCommand : Command()

    class SuccessCommandHandler :
        CommandWithResultHandler<SuccessCommand, Either<AppError, Unit>> {

        override suspend fun handle(command: SuccessCommand): Either<AppError, Unit> = either {
            TxTestTable.insert {
                it[name] = "A"
            }

            TxTestTable.insert {
                it[name] = "B"
            }

            TxTestTable.insert {
                it[name] = "C"
            }
        }
    }

    class FailingCommandHandler :
        CommandWithResultHandler<FailingCommand, Either<AppError, Unit>> {

        override suspend fun handle(command: FailingCommand): Either<AppError, Unit> = either {
            try {
                TxTestTable.insert {
                    it[name] = "A"
                }

                TxTestTable.insert {
                    it[name] = "A"
                }

                TxTestTable.insert {
                    it[name] = "B"
                }
            } catch (e: Exception) {
                raise(object : InfrastructureError("Some error") {})
            }
        }
    }

    @Autowired
    lateinit var mediator: Mediator

    @BeforeEach
    fun clearDb() {
        transaction {
            TxTestTable.deleteAll()
        }
    }

    @Test
    @DisplayName("Should transaction be committed when all operations succeed")
    fun should_transactionBeCommitted_when_allOperationsSucceed(): Unit = runBlocking {
        mediator.send(SuccessCommand())

        val entityCount = transaction {
            return@transaction TxTestTable.selectAll().count()
        }
        entityCount.shouldBe(3)
    }

    @Test
    @DisplayName("Should transaction be rolled back when any of operation fails")
    fun should_transactionBeRolledBack_when_anyOfOperationFails(): Unit = runBlocking {
        mediator.send(FailingCommand())

        val entityCount = transaction {
            return@transaction TxTestTable.selectAll().count()
        }

        entityCount.shouldBe(0)
    }
}