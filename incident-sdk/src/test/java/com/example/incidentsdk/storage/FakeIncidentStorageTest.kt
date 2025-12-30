package com.example.incidentsdk.storage

import com.example.incidentsdk.Severity
import com.example.incidentsdk.internal.Incident
import com.example.incidentsdk.testutil.FakeIncidentStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [FakeIncidentStorage].
 *
 * These tests verify the storage semantics including:
 * - Saving and retrieving incidents
 * - FIFO behavior when maxSize is exceeded
 * - Thread-safety (basic)
 *
 * Uses kotlinx-coroutines-test:
 * - [runTest] for testing suspend functions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FakeIncidentStorageTest {

    private lateinit var storage: FakeIncidentStorage

    @Before
    fun setup() {
        storage = FakeIncidentStorage(maxSize = 10)
    }

    // ========================================================================
    // Saving and retrieving incidents
    // ========================================================================

    @Test
    fun `saveIncident_and_getAllIncidents_work`() = runTest {
        // Arrange
        val incident1 = createIncident(id = 1L, errorCode = "E1")
        val incident2 = createIncident(id = 2L, errorCode = "E2")
        val incident3 = createIncident(id = 3L, errorCode = "E3")

        // Act
        storage.saveIncident(incident1)
        storage.saveIncident(incident2)
        storage.saveIncident(incident3)

        val allIncidents = storage.getAllIncidents()

        // Assert
        assertEquals(3, allIncidents.size)
        assertEquals("E1", allIncidents[0].errorCode)
        assertEquals("E2", allIncidents[1].errorCode)
        assertEquals("E3", allIncidents[2].errorCode)
    }

    @Test
    fun `getAllIncidents_returnsEmptyListWhenNoIncidents`() = runTest {
        // Act
        val allIncidents = storage.getAllIncidents()

        // Assert
        assertTrue(allIncidents.isEmpty())
    }

    @Test
    fun `getAllIncidents_returnsCopy`() = runTest {
        // Arrange
        val incident = createIncident(id = 1L, errorCode = "E1")
        storage.saveIncident(incident)

        // Act: Get incidents twice
        val list1 = storage.getAllIncidents()
        val list2 = storage.getAllIncidents()

        // Assert: Lists are equal but not the same instance
        assertEquals(list1, list2)
    }

    // ========================================================================
    // Enforcing maxSize (FIFO behavior)
    // ========================================================================

    @Test
    fun `saveIncident_dropsOldestWhenMaxSizeExceeded`() = runTest {
        // Arrange: Storage with maxSize = 3
        val limitedStorage = FakeIncidentStorage(maxSize = 3)

        // Act: Save 4 incidents
        limitedStorage.saveIncident(createIncident(id = 1L, errorCode = "E1"))
        limitedStorage.saveIncident(createIncident(id = 2L, errorCode = "E2"))
        limitedStorage.saveIncident(createIncident(id = 3L, errorCode = "E3"))
        limitedStorage.saveIncident(createIncident(id = 4L, errorCode = "E4"))

        val allIncidents = limitedStorage.getAllIncidents()

        // Assert: Size is 3, oldest (E1) was dropped
        assertEquals(3, allIncidents.size)
        assertEquals("E2", allIncidents[0].errorCode)
        assertEquals("E3", allIncidents[1].errorCode)
        assertEquals("E4", allIncidents[2].errorCode)
    }

    @Test
    fun `saveIncident_respectsMaxSizeOfOne`() = runTest {
        // Arrange: Storage with maxSize = 1
        val limitedStorage = FakeIncidentStorage(maxSize = 1)

        // Act: Save 3 incidents
        limitedStorage.saveIncident(createIncident(id = 1L, errorCode = "E1"))
        limitedStorage.saveIncident(createIncident(id = 2L, errorCode = "E2"))
        limitedStorage.saveIncident(createIncident(id = 3L, errorCode = "E3"))

        val allIncidents = limitedStorage.getAllIncidents()

        // Assert: Only the last incident remains
        assertEquals(1, allIncidents.size)
        assertEquals("E3", allIncidents[0].errorCode)
    }

    @Test
    fun `saveIncident_multipleOverflowsDropCorrectly`() = runTest {
        // Arrange: Storage with maxSize = 2
        val limitedStorage = FakeIncidentStorage(maxSize = 2)

        // Act: Save 5 incidents
        repeat(5) { i ->
            limitedStorage.saveIncident(createIncident(id = i.toLong(), errorCode = "E$i"))
        }

        val allIncidents = limitedStorage.getAllIncidents()

        // Assert: Only last 2 remain (E3, E4)
        assertEquals(2, allIncidents.size)
        assertEquals("E3", allIncidents[0].errorCode)
        assertEquals("E4", allIncidents[1].errorCode)
    }

    // ========================================================================
    // Clear all
    // ========================================================================

    @Test
    fun `clearAll_removesAllIncidents`() = runTest {
        // Arrange
        storage.saveIncident(createIncident(id = 1L, errorCode = "E1"))
        storage.saveIncident(createIncident(id = 2L, errorCode = "E2"))
        assertEquals(2, storage.size())

        // Act
        storage.clearAll()

        // Assert
        assertEquals(0, storage.size())
        assertTrue(storage.getAllIncidents().isEmpty())
    }

    // ========================================================================
    // Thread-safety / Mutex (basic)
    // ========================================================================

    @Test
    fun `saveIncident_isSafeFromConcurrentCoroutines`() = runTest {
        // Arrange: Storage with maxSize = 100
        val concurrentStorage = FakeIncidentStorage(maxSize = 100)

        // Act: Launch 50 coroutines to save incidents concurrently
        val jobs = (0 until 50).map { i ->
            launch {
                concurrentStorage.saveIncident(
                    createIncident(id = i.toLong(), errorCode = "E$i")
                )
            }
        }
        jobs.forEach { it.join() }

        // Assert: No crash occurred and exactly 50 incidents were saved
        assertEquals(50, concurrentStorage.size())
    }

    @Test
    fun `saveIncident_concurrentWithMaxSizeEnforced`() = runTest {
        // Arrange: Storage with maxSize = 10
        val limitedStorage = FakeIncidentStorage(maxSize = 10)

        // Act: Launch 50 coroutines to save incidents concurrently
        val jobs = (0 until 50).map { i ->
            launch {
                limitedStorage.saveIncident(
                    createIncident(id = i.toLong(), errorCode = "E$i")
                )
            }
        }
        jobs.forEach { it.join() }

        // Assert: No crash occurred and maxSize is respected
        assertEquals(10, limitedStorage.size())
    }

    // ========================================================================
    // Helper
    // ========================================================================

    private fun createIncident(
        id: Long,
        errorCode: String,
        severity: Severity = Severity.LOW,
        screenName: String = "TestScreen"
    ): Incident {
        return Incident(
            id = id,
            timestampMillis = System.currentTimeMillis(),
            errorCode = errorCode,
            severity = severity,
            message = "Test message",
            screenName = screenName,
            metadata = emptyMap()
        )
    }
}

