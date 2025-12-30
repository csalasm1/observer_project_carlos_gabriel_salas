package com.example.incidentsdk

import android.app.Application
import com.example.incidentsdk.internal.IncidentRepository
import com.example.incidentsdk.internal.IncidentStorage
import com.example.incidentsdk.storage.IncidentDatabase
import com.example.incidentsdk.storage.RoomIncidentStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Main entry point for the Incident SDK.
 *
 * `IncidentTracker` is a singleton facade that provides a simple, thread-safe API
 * for tracking incidents in your Android application. It uses Room database for
 * persistent storage and non-blocking operations using coroutines.
 *
 * ## Initialization
 *
 * The SDK must be initialized before use, typically in your `Application.onCreate()`:
 *
 * ```kotlin
 * class MyApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *
 *         val config = IncidentConfig(
 *             appVersion = BuildConfig.VERSION_NAME,
 *             environment = if (BuildConfig.DEBUG) "debug" else "release",
 *             maxStoredIncidents = 500
 *         )
 *
 *         IncidentTracker.init(this, config)
 *     }
 * }
 * ```
 *
 * ## Tracking Screens
 *
 * Call [trackScreen] when entering a new screen to associate subsequent incidents
 * with that screen:
 *
 * ```kotlin
 * class MainActivity : Activity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         IncidentTracker.trackScreen("MainActivity")
 *     }
 * }
 * ```
 *
 * ## Tracking Incidents
 *
 * Call [trackIncident] when an error or incident occurs:
 *
 * ```kotlin
 * try {
 *     // risky operation
 * } catch (e: Exception) {
 *     IncidentTracker.trackIncident(
 *         errorCode = "NETWORK_001",
 *         severity = Severity.HIGH,
 *         message = "Failed to fetch user data: ${e.message}",
 *         metadata = mapOf("userId" to userId)
 *     )
 * }
 * ```
 *
 * ## Getting Summary
 *
 * Retrieve aggregated incident statistics (call from a coroutine):
 *
 * ```kotlin
 * viewModelScope.launch {
 *     val summary = IncidentTracker.getSummary()
 *     Log.d("Incidents", "Total: ${summary.totalIncidents}")
 * }
 * ```
 *
 * @see IncidentConfig
 * @see Severity
 * @see IncidentSummary
 */
object IncidentTracker {

    @Volatile
    private var initialized = false

    private lateinit var repository: IncidentRepository

    private lateinit var config: IncidentConfig

    @Volatile
    private var currentScreenName: String? = null

    private lateinit var scope: CoroutineScope

    /**
     * Initializes the Incident SDK.
     *
     * This method must be called before any other SDK methods. It is recommended
     * to call this in your `Application.onCreate()` method.
     *
     * If called multiple times, subsequent calls are ignored (no-op).
     *
     * @param app The [Application] instance.
     * @param config The [IncidentConfig] containing SDK configuration options.
     *
     * @throws IllegalStateException If called on a non-main thread during first initialization
     *                               (for Room database creation safety).
     */
    @Synchronized
    fun init(app: Application, config: IncidentConfig) {
        if (initialized) {
            return // Already initialized, no-op
        }

        this.config = config
        this.scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        val database = IncidentDatabase.getInstance(app.applicationContext)
        val storage = RoomIncidentStorage(database, config.maxStoredIncidents)

        this.repository = IncidentRepository(storage, scope)
        this.initialized = true
    }

    /**
     * Tracks the current screen.
     *
     * Call this method when the user navigates to a new screen. The screen name
     * will be automatically associated with subsequent incidents that don't
     * explicitly specify a screen name.
     *
     * This method is non-blocking and returns immediately.
     *
     * @param screenName The name of the current screen (e.g., "MainActivity", "ProfileScreen").
     *
     * @throws IllegalStateException If the SDK has not been initialized via [init].
     */
    fun trackScreen(screenName: String) {
        checkInitialized()
        currentScreenName = screenName
    }

    /**
     * Tracks an incident.
     *
     * Call this method when an error, exception, or notable event occurs in your app.
     * The incident is recorded asynchronously and does not block the calling thread.
     *
     * If no [screenName] is provided, the last screen set via [trackScreen] is used.
     * If no screen has been tracked, "unknown" is used.
     *
     * The following metadata is automatically added to each incident:
     * - `appVersion`: From the [IncidentConfig]
     * - `environment`: From the [IncidentConfig]
     *
     * @param errorCode Application-defined error code for categorizing incidents (e.g., "AUTH_001").
     * @param severity The [Severity] level of the incident.
     * @param message A human-readable description of what happened.
     * @param screenName Optional screen name override. If null, uses the current tracked screen.
     * @param metadata Optional additional key-value pairs providing context about the incident.
     *
     * @throws IllegalStateException If the SDK has not been initialized via [init].
     */
    fun trackIncident(
        errorCode: String,
        severity: Severity,
        message: String,
        screenName: String? = null,
        metadata: Map<String, String> = emptyMap()
    ) {
        checkInitialized()

        val effectiveScreenName = screenName ?: currentScreenName

        trackScreen(effectiveScreenName.orEmpty())

        val enrichedMetadata = metadata.toMutableMap().apply {
            put("appVersion", config.appVersion)
            put("environment", config.environment)
        }

        repository.recordIncident(
            errorCode = errorCode,
            severity = severity,
            message = message,
            screenName = effectiveScreenName,
            metadata = enrichedMetadata
        )
    }

    /**
     * Retrieves a summary of all tracked incidents.
     *
     * This suspend function computes aggregated statistics about stored incidents,
     * including total count, breakdown by screen, and breakdown by severity.
     *
     * @return An [IncidentSummary] containing aggregated incident statistics.
     *
     * @throws IllegalStateException If the SDK has not been initialized via [init].
     */
    suspend fun getSummary(): IncidentSummary {
        checkInitialized()
        return repository.getSummary()
    }

    /**
     * Checks if the SDK has been initialized.
     *
     * @throws IllegalStateException If the SDK has not been initialized.
     */
    private fun checkInitialized() {
        check(initialized) {
            "IncidentTracker has not been initialized. Call IncidentTracker.init(app, config) first, " +
                "typically in your Application.onCreate() method."
        }
    }

    /**
     * Resets the SDK state.
     *
     * This is primarily intended for testing purposes and should not be called
     * in production code.
     */
    @androidx.annotation.VisibleForTesting
    internal fun reset() {
        initialized = false
        currentScreenName = null
    }

    /**
     * Initializes the SDK for testing with a custom repository.
     *
     * This method allows injecting a fake repository for unit testing purposes.
     * Should NOT be used in production code.
     *
     * @param config The configuration to use.
     * @param repository The repository implementation (can be fake for tests).
     * @param testScope The coroutine scope to use for tests.
     */
    @androidx.annotation.VisibleForTesting
    internal fun initForTesting(
        config: IncidentConfig,
        repository: IncidentRepository,
        testScope: CoroutineScope
    ) {
        this.config = config
        this.repository = repository
        this.scope = testScope
        this.initialized = true
        this.currentScreenName = null
    }
}
