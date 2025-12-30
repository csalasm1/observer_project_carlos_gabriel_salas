package com.example.incidentsdk.internal

import com.example.incidentsdk.Severity
import com.example.incidentsdk.testutil.FakeIncidentStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [IncidentRepository].
 *
 * These tests verify the repository's incident recording and summary computation
 * using a fake storage implementation.
 *
 * Uses kotlinx-coroutines-test:
 * - [Dispatchers.Unconfined] for immediate coroutine execution (fire-and-forget)
 * - [runTest] for testing suspend functions
 */
class IncidentRepositoryTest {

    private lateinit var storage: FakeIncidentStorage
    private lateinit var repository: IncidentRepository

    @Before
    fun setup() {
        storage = FakeIncidentStorage(maxSize = 100)
        repository = IncidentRepository(
            storage = storage,
            scope = CoroutineScope(Dispatchers.Unconfined)
        )
    }

    // ========================================================================
    // Recording incidents increases total count
    // ========================================================================

    @Test
    fun `recordIncident_increasesTotalCount`() = runTest {
        // Arrange: No incidents recorded
        var summary = repository.getSummary()
        assertEquals(0, summary.totalIncidents)

        // Act: Record 3 incidents (UnconfinedTestDispatcher executes immediately)
        repository.recordIncident(
            errorCode = "ERR_001",
            severity = Severity.LOW,
            message = "Test error 1",
            screenName = "Screen1",
            metadata = emptyMap()
        )
        repository.recordIncident(
            errorCode = "ERR_002",
            severity = Severity.MEDIUM,
            message = "Test error 2",
            screenName = "Screen2",
            metadata = emptyMap()
        )
        repository.recordIncident(
            errorCode = "ERR_003",
            severity = Severity.HIGH,
            message = "Test error 3",
            screenName = "Screen1",
            metadata = emptyMap()
        )

        // Assert: Summary shows 3 incidents
        summary = repository.getSummary()
        assertEquals(3, summary.totalIncidents)
    }

    // ========================================================================
    // Grouping by screen is correct
    // ========================================================================

    @Test
    fun `getSummary_groupsIncidentsByScreen`() = runTest {
        // Arrange: Record incidents on different screens
        repository.recordIncident("ERR_001", Severity.LOW, "Error 1", "Home", emptyMap())
        repository.recordIncident("ERR_002", Severity.LOW, "Error 2", "Home", emptyMap())
        repository.recordIncident("ERR_003", Severity.LOW, "Error 3", "Detail", emptyMap())
        repository.recordIncident("ERR_004", Severity.LOW, "Error 4", null, emptyMap())

        // Act
        val summary = repository.getSummary()

        // Assert
        assertEquals(4, summary.totalIncidents)
        assertEquals(3, summary.incidentsByScreen.size)
        assertEquals(2, summary.incidentsByScreen["Home"])
        assertEquals(1, summary.incidentsByScreen["Detail"])
        assertEquals(1, summary.incidentsByScreen["unknown"]) // null -> "unknown"
    }

    @Test
    fun `getSummary_mapsNullScreenToUnknown`() = runTest {
        // Arrange: Record incidents with null screen names
        repository.recordIncident("ERR_001", Severity.LOW, "Error 1", null, emptyMap())
        repository.recordIncident("ERR_002", Severity.LOW, "Error 2", null, emptyMap())
        repository.recordIncident("ERR_003", Severity.LOW, "Error 3", "KnownScreen", emptyMap())

        // Act
        val summary = repository.getSummary()

        // Assert
        assertEquals(3, summary.totalIncidents)
        assertEquals(2, summary.incidentsByScreen.size)
        assertEquals(2, summary.incidentsByScreen["unknown"])
        assertEquals(1, summary.incidentsByScreen["KnownScreen"])
    }

    // ========================================================================
    // Grouping by severity is correct
    // ========================================================================

    @Test
    fun `getSummary_groupsIncidentsBySeverity`() = runTest {
        // Arrange: Record incidents with different severities
        repository.recordIncident("E1", Severity.LOW, "m1", "S", emptyMap())
        repository.recordIncident("E2", Severity.CRITICAL, "m2", "S", emptyMap())
        repository.recordIncident("E3", Severity.CRITICAL, "m3", "S", emptyMap())

        // Act
        val summary = repository.getSummary()

        // Assert
        assertEquals(3, summary.totalIncidents)
        assertEquals(1, summary.incidentsBySeverity[Severity.LOW])
        assertEquals(2, summary.incidentsBySeverity[Severity.CRITICAL])
    }

    @Test
    fun `getSummary_groupsAllSeverityLevelsCorrectly`() = runTest {
        // Arrange: Record incidents with all severity levels
        repository.recordIncident("E1", Severity.LOW, "m1", "S", emptyMap())
        repository.recordIncident("E2", Severity.LOW, "m2", "S", emptyMap())
        repository.recordIncident("E3", Severity.MEDIUM, "m3", "S", emptyMap())
        repository.recordIncident("E4", Severity.HIGH, "m4", "S", emptyMap())
        repository.recordIncident("E5", Severity.HIGH, "m5", "S", emptyMap())
        repository.recordIncident("E6", Severity.CRITICAL, "m6", "S", emptyMap())

        // Act
        val summary = repository.getSummary()

        // Assert
        assertEquals(6, summary.totalIncidents)
        assertEquals(4, summary.incidentsBySeverity.size)
        assertEquals(2, summary.incidentsBySeverity[Severity.LOW])
        assertEquals(1, summary.incidentsBySeverity[Severity.MEDIUM])
        assertEquals(2, summary.incidentsBySeverity[Severity.HIGH])
        assertEquals(1, summary.incidentsBySeverity[Severity.CRITICAL])
    }

    // ========================================================================
    // Summary with no incidents returns zeros and empty maps
    // ========================================================================

    @Test
    fun `getSummary_returnsEmptyMapsWhenNoIncidents`() = runTest {
        // Arrange: No incidents recorded (nothing to do)

        // Act
        val summary = repository.getSummary()

        // Assert
        assertEquals(0, summary.totalIncidents)
        assertTrue(summary.incidentsByScreen.isEmpty())
        assertTrue(summary.incidentsBySeverity.isEmpty())
        assertTrue(summary.incidentTimestamps.isEmpty())
        assertTrue(summary.timestampsWithScreen.isEmpty())
    }

    // ========================================================================
    // Additional tests for timestamp and metadata
    // ========================================================================

    @Test
    fun `getSummary_includesTimestampsSortedDescending`() = runTest {
        // Arrange
        repository.recordIncident("E1", Severity.LOW, "m1", "S", emptyMap())
        repository.recordIncident("E2", Severity.HIGH, "m2", "S", emptyMap())
        repository.recordIncident("E3", Severity.CRITICAL, "m3", "S", emptyMap())

        // Act
        val summary = repository.getSummary()

        // Assert: Timestamps are present and sorted descending
        assertEquals(3, summary.incidentTimestamps.size)
        assertTrue(summary.incidentTimestamps.zipWithNext().all { (a, b) -> a >= b })
    }

    @Test
    fun `getSummary_includesTimestampsWithScreen`() = runTest {
        // Arrange
        repository.recordIncident("E1", Severity.LOW, "m1", "Home", emptyMap())
        repository.recordIncident("E2", Severity.HIGH, "m2", "Detail", emptyMap())

        // Act
        val summary = repository.getSummary()

        // Assert
        assertEquals(2, summary.timestampsWithScreen.size)
        assertTrue(summary.timestampsWithScreen.any { it.screenName == "Home" })
        assertTrue(summary.timestampsWithScreen.any { it.screenName == "Detail" })
    }

    // ========================================================================
    // Complex scenario
    // ========================================================================

    @Test
    fun `getSummary_complexScenarioWithMixedScreensAndSeverities`() = runTest {
        // Arrange: Complex mix of incidents
        repository.recordIncident("E1", Severity.LOW, "m1", "Login", emptyMap())
        repository.recordIncident("E2", Severity.HIGH, "m2", "Login", emptyMap())
        repository.recordIncident("E3", Severity.CRITICAL, "m3", "Login", emptyMap())
        repository.recordIncident("E4", Severity.MEDIUM, "m4", "Home", emptyMap())
        repository.recordIncident("E5", Severity.LOW, "m5", "Home", emptyMap())
        repository.recordIncident("E6", Severity.HIGH, "m6", null, emptyMap())
        repository.recordIncident("E7", Severity.CRITICAL, "m7", "Settings", emptyMap())

        // Act
        val summary = repository.getSummary()

        // Assert
        assertEquals(7, summary.totalIncidents)
        assertEquals(7, summary.incidentTimestamps.size)

        // By screen: Login=3, Home=2, unknown=1, Settings=1
        assertEquals(4, summary.incidentsByScreen.size)
        assertEquals(3, summary.incidentsByScreen["Login"])
        assertEquals(2, summary.incidentsByScreen["Home"])
        assertEquals(1, summary.incidentsByScreen["unknown"])
        assertEquals(1, summary.incidentsByScreen["Settings"])

        // By severity: LOW=2, MEDIUM=1, HIGH=2, CRITICAL=2
        assertEquals(4, summary.incidentsBySeverity.size)
        assertEquals(2, summary.incidentsBySeverity[Severity.LOW])
        assertEquals(1, summary.incidentsBySeverity[Severity.MEDIUM])
        assertEquals(2, summary.incidentsBySeverity[Severity.HIGH])
        assertEquals(2, summary.incidentsBySeverity[Severity.CRITICAL])
    }
}

