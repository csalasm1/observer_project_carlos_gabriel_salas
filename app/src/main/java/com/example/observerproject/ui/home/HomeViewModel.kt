package com.example.observerproject.ui.home

import androidx.lifecycle.ViewModel
import com.example.incidentsdk.Severity
import com.example.observerproject.domain.model.ScreenConfig
import com.example.observerproject.domain.usecase.TrackIncidentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trackIncident: TrackIncidentUseCase
) : ViewModel() {

    private val screen = ScreenConfig.Home

    fun onScreenVisible() {
        trackIncident.trackScreen(screen)
    }

    fun onLowIncidentClicked() {
        trackIncident(screen, Severity.LOW)
    }

    fun onMediumIncidentClicked() {
        trackIncident(screen, Severity.MEDIUM)
    }

    fun onHighIncidentClicked() {
        trackIncident(screen, Severity.HIGH)
    }
}
