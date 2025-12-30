package com.example.incidentsdk.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for storing incidents.
 *
 * This is the main database class for the Incident SDK.
 * It provides access to the [IncidentDao] for performing CRUD operations on incidents.
 *
 * The database is accessed through the companion object's [getInstance] method,
 * which ensures a singleton instance is used throughout the app.
 */
@Database(
    entities = [IncidentEntity::class],
    version = 1,
    exportSchema = false
)
internal abstract class IncidentDatabase : RoomDatabase() {

    /**
     * Returns the DAO for incident operations.
     *
     * @return The [IncidentDao] instance.
     */
    abstract fun incidentDao(): IncidentDao

    companion object {
        private const val DATABASE_NAME = "incident_sdk_database"

        @Volatile
        private var instance: IncidentDatabase? = null

        /**
         * Gets the singleton instance of the database.
         *
         * Uses double-checked locking to ensure thread-safe initialization.
         *
         * @param context The application context.
         * @return The singleton [IncidentDatabase] instance.
         */
        fun getInstance(context: Context): IncidentDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): IncidentDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                IncidentDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}

