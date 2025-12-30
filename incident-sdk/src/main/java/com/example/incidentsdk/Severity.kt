package com.example.incidentsdk

/**
 * Represents the severity level of an incident.
 *
 * Severity levels are used to categorize and prioritize incidents based on their impact.
 * This helps in filtering, sorting, and creating meaningful summaries of tracked incidents.
 *
 * @see IncidentTracker.trackIncident
 * @see IncidentSummary.incidentsBySeverity
 */
enum class Severity {
    /**
     * Low severity incident.
     *
     * Represents minor issues that have minimal impact on user experience.
     * Examples: cosmetic bugs, non-critical warnings.
     */
    LOW,

    /**
     * Medium severity incident.
     *
     * Represents issues that affect functionality but have workarounds.
     * Examples: degraded performance, non-blocking errors.
     */
    MEDIUM,

    /**
     * High severity incident.
     *
     * Represents significant issues that impact core functionality.
     * Examples: feature failures, data synchronization issues.
     */
    HIGH,

    /**
     * Critical severity incident.
     *
     * Represents severe issues that cause major failures or data loss.
     * Examples: crashes, security vulnerabilities, complete feature breakdown.
     */
    CRITICAL
}

