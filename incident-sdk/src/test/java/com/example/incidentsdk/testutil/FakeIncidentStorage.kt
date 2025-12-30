package com.example.incidentsdk.testutil

import com.example.incidentsdk.internal.Incident
import com.example.incidentsdk.internal.IncidentStorage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Fake implementation of [IncidentStorage] for unit testing.
 *
 * This implementation stores incidents in memory using an [ArrayDeque] and
 * provides thread-safety using a [Mutex]. It enforces the maximum size limit
 * by removing the oldest incidents (FIFO) when exceeded.
 *
 * @property maxSize Maximum number of incidents to store. When exceeded, the oldest
 *                   incidents are removed (FIFO behavior).
 */
internal class FakeIncidentStorage(
    private val maxSize: Int = 100
) : IncidentStorage {

    private val mutex = Mutex()
    private val incidents = ArrayDeque<Incident>()

    /**
     * Saves an incident to the storage.
     *
     * If the storage is at capacity, the oldest incident is removed before
     * adding the new one (FIFO behavior).
     *
     * Thread-safe via [Mutex].
     */
    override suspend fun saveIncident(incident: Incident) {
        mutex.withLock {
            while (incidents.size >= maxSize) {
                incidents.removeFirstOrNull()
            }
            incidents.addLast(incident)
        }
    }

    /**
     * Retrieves all stored incidents as a copy.
     *
     * @return A new list containing all incidents.
     */
    override suspend fun getAllIncidents(): List<Incident> {
        return mutex.withLock {
            incidents.toList()
        }
    }

    /**
     * Clears all incidents from storage.
     */
    override suspend fun clearAll() {
        mutex.withLock {
            incidents.clear()
        }
    }

    /**
     * Returns the current number of stored incidents.
     * Useful for test assertions.
     */
    suspend fun size(): Int {
        return mutex.withLock {
            incidents.size
        }
    }
}

