package com.example.incidentsdk.storage

import com.example.incidentsdk.Severity
import com.example.incidentsdk.internal.Incident
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Mapper for converting between domain [Incident] and database [IncidentEntity].
 *
 * This object provides utility functions for mapping between the internal domain model
 * and the Room entity model, including JSON serialization of metadata.
 */
internal object IncidentMapper {

    private val gson = Gson()

    /**
     * Converts a domain [Incident] to a database [IncidentEntity].
     *
     * @param incident The domain incident to convert.
     * @return The corresponding database entity.
     */
    fun toEntity(incident: Incident): IncidentEntity {
        return IncidentEntity(
            id = incident.id,
            timestampMillis = incident.timestampMillis,
            errorCode = incident.errorCode,
            severity = incident.severity.name,
            message = incident.message,
            screenName = incident.screenName,
            metadataJson = gson.toJson(incident.metadata)
        )
    }

    /**
     * Converts a database [IncidentEntity] to a domain [Incident].
     *
     * @param entity The database entity to convert.
     * @return The corresponding domain incident.
     */
    fun toDomain(entity: IncidentEntity): Incident {
        val metadataType = object : TypeToken<Map<String, String>>() {}.type
        val metadata: Map<String, String> = try {
            gson.fromJson(entity.metadataJson, metadataType) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }

        return Incident(
            id = entity.id,
            timestampMillis = entity.timestampMillis,
            errorCode = entity.errorCode,
            severity = try {
                Severity.valueOf(entity.severity)
            } catch (e: IllegalArgumentException) {
                Severity.LOW // Default fallback
            },
            message = entity.message,
            screenName = entity.screenName,
            metadata = metadata
        )
    }
}

