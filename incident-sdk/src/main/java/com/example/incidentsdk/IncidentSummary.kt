package com.example.incidentsdk

/**
 * Represents a timestamp with its associated screen name.
 *
 * @property timestampMillis The timestamp in milliseconds when the incident occurred.
 * @property screenName The screen where the incident occurred (null if unknown).
 */
data class TimestampWithScreen(
    val timestampMillis: Long,
    val screenName: String?
)

/**
 * Summary statistics for tracked incidents.
 *
 * This data class provides aggregated information about all stored incidents,
 * including total counts and breakdowns by screen, severity, and timestamp.
 *
 * ## Usage Example
 * ```kotlin
 * val summary = IncidentTracker.getSummary()
 *
 * Log.d("Incidents", "Total: ${summary.totalIncidents}")
 * summary.incidentsByScreen.forEach { (screen, count) ->
 *     Log.d("Incidents", "Screen '$screen': $count incidents")
 * }
 * summary.incidentsBySeverity.forEach { (severity, count) ->
 *     Log.d("Incidents", "Severity $severity: $count incidents")
 * }
 * ```
 *
 * @property totalIncidents The total number of incidents currently stored.
 * @property incidentsByScreen A map of screen names to incident counts.
 *                             Incidents without a screen name are grouped under "unknown".
 * @property incidentsBySeverity A map of severity levels to incident counts.
 * @property incidentTimestamps A list of timestamps (in milliseconds) when incidents occurred.
 *                              Used for time-based filtering and charting.
 * @property timestampsWithScreen A list of timestamps with their associated screen names.
 *                                Used for screen-based time charting.
 *
 * @see IncidentTracker.getSummary
 * @see Severity
 */
data class IncidentSummary(
    val totalIncidents: Int,
    val incidentsByScreen: Map<String, Int>,
    val incidentsBySeverity: Map<Severity, Int>,
    val incidentTimestamps: List<Long> = emptyList(),
    val timestampsWithScreen: List<TimestampWithScreen> = emptyList()
)
