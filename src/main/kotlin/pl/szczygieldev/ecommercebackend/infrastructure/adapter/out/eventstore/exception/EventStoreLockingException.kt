package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.eventstore.exception

import pl.szczygieldev.ddd.core.Identity

class EventStoreLockingException(val aggregateId: Identity<*>,val currentVersion: Int?, val exceptedVersion: Int) : RuntimeException("Optimistic locking exception for aggregate='${aggregateId.id()}' version='$currentVersion exceptedVersion=$exceptedVersion'")