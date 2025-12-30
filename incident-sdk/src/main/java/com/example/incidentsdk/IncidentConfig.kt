package com.example.incidentsdk

/**
 * Configuration class for the Incident SDK.
 *
 * This data class holds all the configuration options needed to initialize the [IncidentTracker].
 * It allows customization of the environment metadata and storage limits.
 *
 * ## Usage Example
 * ```kotlin
 * val config = IncidentConfig(
 *     appVersion = BuildConfig.VERSION_NAME,
 *     environment = if (BuildConfig.DEBUG) "debug" else "release",
 *     maxStoredIncidents = 500
 * )
 *
 * IncidentTracker.init(application, config)
 * ```
 *
 * @property appVersion The version of the host application (e.g., "1.0.0").
 *                      This is automatically added as metadata to all tracked incidents.
 * @property environment The current environment (e.g., "debug", "staging", "production").
 *                       Defaults to "debug". Added as metadata to all tracked incidents.
 * @property storageType The storage backend to use for persisting incidents.
 *                       Defaults to [StorageType.Room].
 * @property maxStoredIncidents The maximum number of incidents to store.
 *                              When exceeded, oldest incidents are removed (FIFO).
 *                              Defaults to 1000.
 *
 * @see IncidentTracker
 * @see StorageType
 */
data class IncidentConfig(
    val appVersion: String,
    val environment: String = "debug",
    val storageType: StorageType = StorageType.Room,
    val maxStoredIncidents: Int = 1_000
)
