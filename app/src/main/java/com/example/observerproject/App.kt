package com.example.observerproject

import android.app.Application
import com.example.incidentsdk.IncidentConfig
import com.example.incidentsdk.IncidentTracker
import com.example.incidentsdk.StorageType
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        IncidentTracker.init(
            app = this,
            config = IncidentConfig(
                appVersion = BuildConfig.VERSION_NAME,
                environment = if (BuildConfig.DEBUG) "debug" else "release",
                storageType = StorageType.Room,
                maxStoredIncidents = 1000
            )
        )
    }
}
