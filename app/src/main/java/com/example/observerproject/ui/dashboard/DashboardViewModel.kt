package com.example.observerproject.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.incidentsdk.IncidentTracker
import com.example.incidentsdk.Severity
import com.example.incidentsdk.TimestampWithScreen
import com.example.observerproject.domain.model.ScreenConfig
import com.example.observerproject.domain.usecase.TrackIncidentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val trackIncident: TrackIncidentUseCase
) : ViewModel() {

    companion object {
        private const val REFRESH_DELAY_MS = 100L
    }

    private val screen = ScreenConfig.Dashboard

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {
            refreshTrigger.collectLatest {
                delay(REFRESH_DELAY_MS)
                loadSummaryInternal()
            }
        }
        loadSummary()
    }

    fun loadSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            loadSummaryInternal()
        }
    }

    private suspend fun loadSummaryInternal() {
        try {
            val summary = IncidentTracker.getSummary()

            val incidentsByScreen = summary.incidentsByScreen
                .toList()
                .sortedByDescending { it.second }

            val severityOrder = listOf(
                Severity.CRITICAL,
                Severity.HIGH,
                Severity.MEDIUM,
                Severity.LOW
            )

            val incidentsBySeverity = severityOrder
                .mapNotNull { severity ->
                    summary.incidentsBySeverity[severity]?.let { count ->
                        severity to count
                    }
                }
            val currentFilter = _uiState.value.selectedTimeFilter
            val chartData = calculateTimeChartData(summary.timestampsWithScreen, currentFilter)
            val incidentsInRange = chartData.sumOf { it.count }
            val maxValue = chartData.maxOfOrNull { it.count } ?: 0

            _uiState.update {
                it.copy(
                    isLoading = false,
                    totalIncidents = summary.totalIncidents,
                    incidentsByScreen = incidentsByScreen,
                    incidentsBySeverity = incidentsBySeverity,
                    incidentTimestamps = summary.incidentTimestamps,
                    timestampsWithScreen = summary.timestampsWithScreen,
                    chartData = chartData,
                    incidentsInTimeRange = incidentsInRange,
                    maxChartValue = maxValue,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun onTimeFilterSelected(filter: TimeFilter) {
        val timestampsWithScreen = _uiState.value.timestampsWithScreen
        val chartData = calculateTimeChartData(timestampsWithScreen, filter)
        val incidentsInRange = chartData.sumOf { it.count }
        val maxValue = chartData.maxOfOrNull { it.count } ?: 0

        _uiState.update {
            it.copy(
                selectedTimeFilter = filter,
                chartData = chartData,
                incidentsInTimeRange = incidentsInRange,
                maxChartValue = maxValue
            )
        }
    }

    fun refresh() {
        loadSummary()
    }

    fun onLowIncidentClicked() {
        trackIncident(screen, Severity.LOW)
        refreshTrigger.tryEmit(Unit)
    }

    fun onMediumIncidentClicked() {
        trackIncident(screen, Severity.MEDIUM)
        refreshTrigger.tryEmit(Unit)
    }

    fun onHighIncidentClicked() {
        trackIncident(screen, Severity.HIGH)
        refreshTrigger.tryEmit(Unit)
    }

    private fun calculateTimeChartData(
        timestampsWithScreen: List<TimestampWithScreen>,
        filter: TimeFilter
    ): List<TimeChartDataPoint> {
        val now = System.currentTimeMillis()
        val filterMillis = filter.minutes * 60 * 1000L
        val cutoffTime = now - filterMillis

        val filteredTimestamps = timestampsWithScreen.filter { it.timestampMillis >= cutoffTime }

        val minuteGroups = filteredTimestamps.groupBy { item ->
            val minutesAgo = ((now - item.timestampMillis) / 60_000).toInt()
            -minutesAgo
        }

        val dataPoints = mutableListOf<TimeChartDataPoint>()
        for (minuteOffset in -(filter.minutes - 1)..0) {
            val itemsInMinute = minuteGroups[minuteOffset] ?: emptyList()
            val totalCount = itemsInMinute.size
            val homeCount = itemsInMinute.count { it.screenName == ScreenConfig.Home.name }
            val detailCount = itemsInMinute.count { it.screenName == ScreenConfig.Detail.name }
            val dashboardCount = itemsInMinute.count { it.screenName == ScreenConfig.Dashboard.name }

            dataPoints.add(
                TimeChartDataPoint(
                    minuteOffset = minuteOffset,
                    count = totalCount,
                    homeCount = homeCount,
                    detailCount = detailCount,
                    dashboardCount = dashboardCount
                )
            )
        }

        return dataPoints
    }
}
