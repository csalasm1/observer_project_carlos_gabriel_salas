package com.example.incidentsdk.internal

import com.example.incidentsdk.IncidentSummary
import com.example.incidentsdk.Severity
import com.example.incidentsdk.TimestampWithScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repository layer for managing incidents.
 *
 * This class acts as an intermediary between the public API ([com.example.incidentsdk.IncidentTracker])
 * and the storage layer ([IncidentStorage]). It handles incident creation and summary computation.
 *
 * @property storage The storage implementation to use for persisting incidents.
 * @property scope The coroutine scope to use for fire-and-forget operations.
 */
internal class IncidentRepository(
    private val storage: IncidentStorage,
    private val scope: CoroutineScope
) {

    /**
     * Records a new incident.
     *
     * This method creates an [Incident] with a generated ID and current timestamp,
     * then saves it to storage in a fire-and-forget manner (does not block the caller).
     *
     * @param errorCode Application-defined error code.
     * @param severity The severity level of the incident.
     * @param message Human-readable description of the incident.
     * @param screenName The name of the screen where the incident occurred.
     * @param metadata Additional key-value pairs providing context.
     */
    fun recordIncident(
        errorCode: String,
        severity: Severity,
        message: String,
        screenName: String?,
        metadata: Map<String, String>
    ) {
        val incident = Incident(
            id = System.currentTimeMillis(),
            timestampMillis = System.currentTimeMillis(),
            errorCode = errorCode,
            severity = severity,
            message = message,
            screenName = screenName,
            metadata = metadata
        )

        scope.launch {
            storage.saveIncident(incident)
        }
    }

    /**
     * Computes and returns a summary of all stored incidents.
     *
     * This method retrieves all incidents from storage and computes:
     * - Total count of incidents
     * - Grouping by screen name (null screens are grouped as "unknown")
     * - Grouping by severity level
     * - List of timestamps for time-based analysis
     * - List of timestamps with screen names for screen-based charting
     *
     * @return An [IncidentSummary] containing aggregated statistics.
     */
    suspend fun getSummary(): IncidentSummary = withContext(Dispatchers.Default) {
        val allIncidents = storage.getAllIncidents()

        val totalIncidents = allIncidents.size

        val incidentsByScreen = allIncidents
            .groupBy { it.screenName ?: "unknown" }
            .mapValues { (_, incidents) -> incidents.size }

        val incidentsBySeverity = allIncidents
            .groupBy { it.severity }
            .mapValues { (_, incidents) -> incidents.size }

        val incidentTimestamps = allIncidents
            .map { it.timestampMillis }
            .sortedDescending()

        val timestampsWithScreen = allIncidents
            .map { TimestampWithScreen(it.timestampMillis, it.screenName) }
            .sortedByDescending { it.timestampMillis }

        IncidentSummary(
            totalIncidents = totalIncidents,
            incidentsByScreen = incidentsByScreen,
            incidentsBySeverity = incidentsBySeverity,
            incidentTimestamps = incidentTimestamps,
            timestampsWithScreen = timestampsWithScreen
        )
    }
}
