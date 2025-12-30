package com.example.incidentsdk.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for incident database operations.
 *
 * This interface defines the database operations for the incidents table.
 * Room generates the implementation at compile time.
 */
@Dao
internal interface IncidentDao {

    /**
     * Inserts an incident into the database.
     *
     * If an incident with the same ID already exists, it will be replaced.
     *
     * @param incident The incident entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incident: IncidentEntity)

    /**
     * Retrieves all incidents from the database.
     *
     * Results are ordered by timestamp in ascending order (oldest first).
     *
     * @return A list of all incident entities.
     */
    @Query("SELECT * FROM incidents ORDER BY timestampMillis ASC")
    suspend fun getAll(): List<IncidentEntity>

    /**
     * Clears all incidents from the database.
     */
    @Query("DELETE FROM incidents")
    suspend fun clearAll()

    /**
     * Gets the total count of incidents in the database.
     *
     * @return The number of incidents stored.
     */
    @Query("SELECT COUNT(*) FROM incidents")
    suspend fun getCount(): Int

    /**
     * Deletes the oldest incidents to maintain the storage limit.
     *
     * This query deletes incidents with the oldest timestamps,
     * keeping only the newest ones within the limit.
     *
     * @param keepCount The number of newest incidents to keep.
     */
    @Query("""
        DELETE FROM incidents 
        WHERE id NOT IN (
            SELECT id FROM incidents 
            ORDER BY timestampMillis DESC 
            LIMIT :keepCount
        )
    """)
    suspend fun deleteOldest(keepCount: Int)
}

