package com.example.incidentsdk.internal

import com.example.incidentsdk.Severity

/**
 * Internal domain model representing a single tracked incident.
 *
 * This class is internal to the SDK and not exposed to consumers.
 * It contains all the information about an incident that occurred in the application.
 *
 * @property id Unique identifier for the incident (generated using timestamp).
 * @property timestampMillis Unix timestamp in milliseconds when the incident was recorded.
 * @property errorCode Application-defined error code for categorizing the incident.
 * @property severity The severity level of the incident.
 * @property message Human-readable description of the incident.
 * @property screenName The name of the screen where the incident occurred (null if unknown).
 * @property metadata Additional key-value pairs providing context about the incident.
 */
internal data class Incident(
    val id: Long,
    val timestampMillis: Long,
    val errorCode: String,
    val severity: Severity,
    val message: String,
    val screenName: String?,
    val metadata: Map<String, String>
)

