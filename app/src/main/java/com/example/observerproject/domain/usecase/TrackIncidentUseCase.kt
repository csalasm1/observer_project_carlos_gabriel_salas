package com.example.observerproject.domain.usecase

import com.example.incidentsdk.IncidentTracker
import com.example.incidentsdk.Severity
import com.example.observerproject.domain.model.ScreenConfig
import javax.inject.Inject

class TrackIncidentUseCase @Inject constructor() {

    companion object {
        private const val KEY_FEATURE = "feature"
        private const val KEY_MODULE = "module"
        private const val KEY_IS_BLOCKING = "isBlocking"
        private const val KEY_SIMULATED_BY = "simulatedBy"
        private const val KEY_CRASH_TYPE = "crashType"

        private const val VALUE_USER_ACTION = "user_action"
        private const val VALUE_TRUE = "true"
        private const val VALUE_FALSE = "false"
        private const val VALUE_CRASH_SIMULATED = "simulated"
    }

    operator fun invoke(
        screen: ScreenConfig,
        severity: Severity,
        isBlocking: Boolean = severity.isBlockingByDefault()
    ) {
        val errorCode = buildErrorCode(screen, severity)
        val message = buildMessage(screen, severity)
        val metadata = buildMetadata(screen, isBlocking)

        IncidentTracker.trackIncident(
            errorCode = errorCode,
            severity = severity,
            message = message,
            screenName = screen.name,
            metadata = metadata
        )
    }

    fun trackCrash(
        screen: ScreenConfig,
        message: String = "Simulated crash triggered from ${screen.name} screen"
    ) {
        val errorCode = "${screen.errorCodePrefix}_CRASH"
        val metadata = buildMetadata(screen, isBlocking = true) + mapOf(
            KEY_CRASH_TYPE to VALUE_CRASH_SIMULATED
        )

        IncidentTracker.trackIncident(
            errorCode = errorCode,
            severity = Severity.CRITICAL,
            message = message,
            screenName = screen.name,
            metadata = metadata
        )
    }

    fun trackScreen(screen: ScreenConfig) {
        IncidentTracker.trackScreen(screen.name)
    }

    private fun buildErrorCode(screen: ScreenConfig, severity: Severity): String {
        return "${screen.errorCodePrefix}_${severity.name}"
    }

    private fun buildMessage(screen: ScreenConfig, severity: Severity): String {
        val severityDisplay = severity.name.lowercase().replaceFirstChar { it.uppercase() }
        return "$severityDisplay severity incident simulated from ${screen.name} screen"
    }

    private fun buildMetadata(screen: ScreenConfig, isBlocking: Boolean): Map<String, String> {
        return mapOf(
            KEY_FEATURE to screen.feature,
            KEY_MODULE to screen.module,
            KEY_IS_BLOCKING to if (isBlocking) VALUE_TRUE else VALUE_FALSE,
            KEY_SIMULATED_BY to VALUE_USER_ACTION
        )
    }

    private fun Severity.isBlockingByDefault(): Boolean {
        return this == Severity.HIGH || this == Severity.CRITICAL
    }
}
