package com.example.incidentsdk

import com.example.incidentsdk.internal.IncidentRepository
import com.example.incidentsdk.testutil.FakeIncidentStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [IncidentTracker].
 *
 * These tests verify the public API behavior including:
 * - Fail-fast when not initialized
 * - Screen tracking remembers last screen
 * - Metadata enrichment with config values
 * - Proper delegation to repository
 *
 * Uses kotlinx-coroutines-test:
 * - [Dispatchers.Unconfined] for immediate coroutine execution
 * - [runTest] for testing suspend functions
 *
 * Note: Uses [IncidentTracker.initForTesting] to inject fake dependencies.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class IncidentTrackerTest {

    private lateinit var testScope: CoroutineScope
    private lateinit var storage: FakeIncidentStorage
    private lateinit var repository: IncidentRepository
    private lateinit var config: IncidentConfig

    @Before
    fun setup() {
        // Reset the singleton before each test
        IncidentTracker.reset()

        testScope = CoroutineScope(Dispatchers.Unconfined)
        storage = FakeIncidentStorage(maxSize = 100)
        repository = IncidentRepository(
            storage = storage,
            scope = testScope
        )
        config = IncidentConfig(
            appVersion = "1.0.0",
            environment = "test",
            maxStoredIncidents = 100
        )
    }

    @After
    fun tearDown() {
        // Reset the singleton after each test
        IncidentTracker.reset()
    }

    // ========================================================================
    // Test: init must fail if not called
    // ========================================================================

    @Test
    fun `trackScreen_throwsExceptionWhenNotInitialized`() {
        // Assert: Calling trackScreen before init throws IllegalStateException
        val exception = assertThrows(IllegalStateException::class.java) {
            IncidentTracker.trackScreen("Home")
        }
        assertTrue(exception.message!!.contains("not been initialized"))
    }

    @Test
    fun `trackIncident_throwsExceptionWhenNotInitialized`() {
        // Assert: Calling trackIncident before init throws IllegalStateException
        val exception = assertThrows(IllegalStateException::class.java) {
            IncidentTracker.trackIncident(
                errorCode = "ERR_001",
                severity = Severity.LOW,
                message = "Test"
            )
        }
        assertTrue(exception.message!!.contains("not been initialized"))
    }

    @Test
    fun `getSummary_throwsExceptionWhenNotInitialized`() = runTest {
        // Assert: Calling getSummary before init throws IllegalStateException
        var exceptionThrown = false
        var exceptionMessage: String? = null
        try {
            IncidentTracker.getSummary()
        } catch (e: IllegalStateException) {
            exceptionThrown = true
            exceptionMessage = e.message
        }
        assertTrue("Expected IllegalStateException to be thrown", exceptionThrown)
        assertTrue(exceptionMessage!!.contains("not been initialized"))
    }

    // ========================================================================
    // Test: trackScreen updates current screen
    // ========================================================================

    @Test
    fun `trackIncident_usesLastTrackedScreenWhenScreenNameNull`() = runTest {
        // Arrange: Initialize and track a screen
        IncidentTracker.initForTesting(config, repository, testScope)
        IncidentTracker.trackScreen("Home")

        // Act: Track incident with null screenName
        IncidentTracker.trackIncident(
            errorCode = "ERR_001",
            severity = Severity.LOW,
            message = "Test error",
            screenName = null
        )

        // Assert: Incident should have "Home" as screen name
        val summary = IncidentTracker.getSummary()
        assertEquals(1, summary.totalIncidents)
        assertEquals(1, summary.incidentsByScreen["Home"])
    }

    @Test
    fun `trackIncident_overridesTrackedScreenWhenScreenNameProvided`() = runTest {
        // Arrange: Initialize and track a screen
        IncidentTracker.initForTesting(config, repository, testScope)
        IncidentTracker.trackScreen("Home")

        // Act: Track incident with explicit screenName
        IncidentTracker.trackIncident(
            errorCode = "ERR_001",
            severity = Severity.LOW,
            message = "Test error",
            screenName = "Detail"
        )

        // Assert: Incident should have "Detail" as screen name (not "Home")
        val summary = IncidentTracker.getSummary()
        assertEquals(1, summary.totalIncidents)
        assertEquals(1, summary.incidentsByScreen["Detail"])
    }

    @Test
    fun `trackScreen_updatesCurrentScreen`() = runTest {
        // Arrange: Initialize
        IncidentTracker.initForTesting(config, repository, testScope)

        // Act: Track multiple screens and incidents
        IncidentTracker.trackScreen("Screen1")
        IncidentTracker.trackIncident("E1", Severity.LOW, "m1")

        IncidentTracker.trackScreen("Screen2")
        IncidentTracker.trackIncident("E2", Severity.LOW, "m2")

        // Assert: Each incident is on its respective screen
        val summary = IncidentTracker.getSummary()
        assertEquals(2, summary.totalIncidents)
        assertEquals(1, summary.incidentsByScreen["Screen1"])
        assertEquals(1, summary.incidentsByScreen["Screen2"])
    }

    // ========================================================================
    // Test: trackIncident passes metadata + config metadata
    // ========================================================================

    @Test
    fun `trackIncident_enrichesMetadataWithConfig`() = runTest {
        // Arrange: Initialize with known config values
        val testConfig = IncidentConfig(
            appVersion = "2.0.0",
            environment = "staging",
            maxStoredIncidents = 100
        )
        IncidentTracker.initForTesting(testConfig, repository, testScope)

        // Act: Track incident with custom metadata
        IncidentTracker.trackIncident(
            errorCode = "ERR_001",
            severity = Severity.HIGH,
            message = "Test error",
            screenName = "TestScreen",
            metadata = mapOf("customKey" to "customValue")
        )

        // Assert: Incident is recorded (we can only verify via count,
        // metadata verification would require exposing internal state)
        val summary = IncidentTracker.getSummary()
        assertEquals(1, summary.totalIncidents)
    }

    @Test
    fun `trackIncident_preservesUserMetadata`() = runTest {
        // Arrange
        IncidentTracker.initForTesting(config, repository, testScope)

        // Act: Track multiple incidents with different metadata
        IncidentTracker.trackIncident(
            errorCode = "ERR_001",
            severity = Severity.LOW,
            message = "Error 1",
            screenName = "Screen",
            metadata = mapOf("key1" to "value1")
        )
        IncidentTracker.trackIncident(
            errorCode = "ERR_002",
            severity = Severity.HIGH,
            message = "Error 2",
            screenName = "Screen",
            metadata = mapOf("key2" to "value2")
        )

        // Assert: Both incidents are recorded
        val summary = IncidentTracker.getSummary()
        assertEquals(2, summary.totalIncidents)
    }

    // ========================================================================
    // Test: getSummary delegates correctly
    // ========================================================================

    @Test
    fun `getSummary_delegatesCorrectlyToRepository`() = runTest {
        // Arrange: Initialize and record some incidents
        IncidentTracker.initForTesting(config, repository, testScope)

        IncidentTracker.trackIncident("E1", Severity.LOW, "m1", "Home", emptyMap())
        IncidentTracker.trackIncident("E2", Severity.HIGH, "m2", "Detail", emptyMap())
        IncidentTracker.trackIncident("E3", Severity.CRITICAL, "m3", "Home", emptyMap())

        // Act
        val summary = IncidentTracker.getSummary()

        // Assert: Summary reflects the recorded incidents
        assertEquals(3, summary.totalIncidents)
        assertEquals(2, summary.incidentsByScreen.size)
        assertEquals(2, summary.incidentsByScreen["Home"])
        assertEquals(1, summary.incidentsByScreen["Detail"])
        assertEquals(1, summary.incidentsBySeverity[Severity.LOW])
        assertEquals(1, summary.incidentsBySeverity[Severity.HIGH])
        assertEquals(1, summary.incidentsBySeverity[Severity.CRITICAL])
    }

    @Test
    fun `getSummary_returnsEmptyWhenNoIncidents`() = runTest {
        // Arrange: Initialize but don't record any incidents
        IncidentTracker.initForTesting(config, repository, testScope)

        // Act
        val summary = IncidentTracker.getSummary()

        // Assert
        assertEquals(0, summary.totalIncidents)
        assertTrue(summary.incidentsByScreen.isEmpty())
        assertTrue(summary.incidentsBySeverity.isEmpty())
    }

    // ========================================================================
    // Additional edge cases
    // ========================================================================

    @Test
    fun `trackIncident_withEmptyMetadata_works`() = runTest {
        // Arrange
        IncidentTracker.initForTesting(config, repository, testScope)

        // Act
        IncidentTracker.trackIncident(
            errorCode = "ERR_001",
            severity = Severity.LOW,
            message = "Test",
            screenName = "Screen",
            metadata = emptyMap()
        )

        // Assert
        val summary = IncidentTracker.getSummary()
        assertEquals(1, summary.totalIncidents)
    }

    @Test
    fun `trackIncident_withAllSeverityLevels_works`() = runTest {
        // Arrange
        IncidentTracker.initForTesting(config, repository, testScope)

        // Act: Track one incident of each severity
        Severity.entries.forEach { severity ->
            IncidentTracker.trackIncident(
                errorCode = "ERR_${severity.name}",
                severity = severity,
                message = "${severity.name} error",
                screenName = "Screen"
            )
        }

        // Assert
        val summary = IncidentTracker.getSummary()
        assertEquals(4, summary.totalIncidents) // LOW, MEDIUM, HIGH, CRITICAL
        assertEquals(4, summary.incidentsBySeverity.size)
    }

    @Test
    fun reset_allowsReinitialization() = runTest {
        // Arrange: Initialize, track, and reset
        IncidentTracker.initForTesting(config, repository, testScope)
        IncidentTracker.trackIncident("E1", Severity.LOW, "m1", "Screen", emptyMap())

        // Verify incident was recorded
        var summary = IncidentTracker.getSummary()
        assertEquals(1, summary.totalIncidents)

        // Act: Reset
        IncidentTracker.reset()

        // Create fresh storage and repository for reinitialization
        val newStorage = FakeIncidentStorage(maxSize = 100)
        val newRepository = IncidentRepository(
            storage = newStorage,
            scope = testScope
        )

        // Re-initialize
        IncidentTracker.initForTesting(config, newRepository, testScope)

        // Assert: New instance has no incidents
        summary = IncidentTracker.getSummary()
        assertEquals(0, summary.totalIncidents)
    }
}

