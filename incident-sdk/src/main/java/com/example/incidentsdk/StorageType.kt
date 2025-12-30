package com.example.incidentsdk

/**
 * Defines the storage backend type for persisting incidents.
 *
 * Currently, the SDK supports Room database storage for persistent incident tracking.
 *
 * @see IncidentConfig
 * @see IncidentTracker
 */
enum class StorageType {
    Room
}
