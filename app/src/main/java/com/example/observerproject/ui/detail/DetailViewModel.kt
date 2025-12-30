package com.example.observerproject.ui.detail

import androidx.lifecycle.ViewModel
import com.example.incidentsdk.Severity
import com.example.observerproject.BuildConfig
import com.example.observerproject.domain.model.ScreenConfig
import com.example.observerproject.domain.usecase.TrackIncidentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val trackIncident: TrackIncidentUseCase
) : ViewModel() {

    private val screen = ScreenConfig.Detail

    fun onScreenVisible() {
        trackIncident.trackScreen(screen)
    }

    fun onLowIncidentClicked() {
        trackIncident(screen, Severity.LOW)
    }

    fun onHighIncidentClicked() {
        trackIncident(screen, Severity.HIGH)
    }

    fun onCriticalIncidentClicked() {
        trackIncident(screen, Severity.CRITICAL)
    }

    fun onCrashClicked() {
        trackIncident.trackCrash(screen)

        if (BuildConfig.DEBUG) {
            throw RuntimeException("Simulated crash from ${screen.name} screen")
        }
    }
}
