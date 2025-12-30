package com.example.incidentsdk.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an incident in the database.
 *
 * This class maps to the "incidents" table in the Room database.
 * It mirrors the internal [com.example.incidentsdk.internal.Incident] domain model
 * with appropriate database-compatible types.
 *
 * @property id Unique identifier for the incident (auto-generated primary key).
 * @property timestampMillis Unix timestamp in milliseconds when the incident occurred.
 * @property errorCode Application-defined error code.
 * @property severity The severity level as a string (e.g., "LOW", "HIGH").
 * @property message Human-readable description of the incident.
 * @property screenName The name of the screen where the incident occurred (nullable).
 * @property metadataJson JSON string representation of the metadata map.
 */
@Entity(tableName = "incidents")
internal data class IncidentEntity(
    @PrimaryKey
    val id: Long,
    val timestampMillis: Long,
    val errorCode: String,
    val severity: String,
    val message: String,
    val screenName: String?,
    val metadataJson: String
)

