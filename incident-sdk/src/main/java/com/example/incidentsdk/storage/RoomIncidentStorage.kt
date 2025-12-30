package com.example.incidentsdk.storage

import com.example.incidentsdk.internal.Incident
import com.example.incidentsdk.internal.IncidentStorage

/**
 * Room-backed implementation of [IncidentStorage].
 *
 * This implementation persists incidents to a SQLite database using Room.
 * Data survives app restarts and is suitable for production use.
 *
 * @property db The Room database instance.
 * @property maxSize Maximum number of incidents to store. When exceeded, the oldest
 *                   incidents are deleted to maintain the limit.
 */
internal class RoomIncidentStorage(
    private val db: IncidentDatabase,
    private val maxSize: Int
) : IncidentStorage {

    private val dao: IncidentDao = db.incidentDao()

    /**
     * Saves an incident to the Room database.
     *
     * After insertion, if the total count exceeds [maxSize], the oldest incidents
     * are deleted to maintain the storage limit.
     *
     * @param incident The incident to save.
     */
    override suspend fun saveIncident(incident: Incident) {
        val entity = IncidentMapper.toEntity(incident)
        dao.insert(entity)

        // Check if we need to trim old incidents
        val currentCount = dao.getCount()
        if (currentCount > maxSize) {
            dao.deleteOldest(maxSize)
        }
    }

    /**
     * Retrieves all stored incidents from the database.
     *
     * @return A list of all incidents, converted from database entities to domain models.
     */
    override suspend fun getAllIncidents(): List<Incident> {
        return dao.getAll().map { entity ->
            IncidentMapper.toDomain(entity)
        }
    }

    /**
     * Clears all incidents from the database.
     */
    override suspend fun clearAll() {
        dao.clearAll()
    }
}

