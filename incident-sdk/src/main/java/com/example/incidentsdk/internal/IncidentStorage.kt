package com.example.incidentsdk.internal

/**
 * Storage abstraction for persisting incidents.
 *
 * This interface defines the contract for incident storage implementations,
 * allowing the SDK to support multiple storage backends (in-memory, Room, etc.).
 *
 * All methods are suspend functions to support non-blocking I/O operations.
 *
 * @see com.example.incidentsdk.storage.InMemoryIncidentStorage
 * @see com.example.incidentsdk.storage.RoomIncidentStorage
 */
internal interface IncidentStorage {

    /**
     * Saves an incident to the storage.
     *
     * Implementations should handle the maximum storage limit and remove
     * the oldest incidents when the limit is exceeded (FIFO behavior).
     *
     * @param incident The incident to save.
     */
    suspend fun saveIncident(incident: Incident)

    /**
     * Retrieves all stored incidents.
     *
     * @return A list of all incidents in storage. Returns an empty list if no incidents exist.
     */
    suspend fun getAllIncidents(): List<Incident>

    /**
     * Clears all incidents from storage.
     *
     * This operation removes all stored incidents permanently.
     */
    suspend fun clearAll()
}

