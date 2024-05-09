import java.time.Instant

interface DomainEvent {

    val occurredOn: Instant

}