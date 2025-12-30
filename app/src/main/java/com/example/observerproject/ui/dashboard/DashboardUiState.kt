package com.example.observerproject.ui.dashboard

import com.example.incidentsdk.Severity
import com.example.incidentsdk.TimestampWithScreen

data class DashboardUiState(
    val isLoading: Boolean = true,
    val totalIncidents: Int = 0,
    val incidentsByScreen: List<Pair<String, Int>> = emptyList(),
    val incidentsBySeverity: List<Pair<Severity, Int>> = emptyList(),
    val errorMessage: String? = null,
    
    val incidentTimestamps: List<Long> = emptyList(),
    val timestampsWithScreen: List<TimestampWithScreen> = emptyList(),
    val selectedTimeFilter: TimeFilter = TimeFilter.LAST_30_MINUTES,
    val chartData: List<TimeChartDataPoint> = emptyList(),
    val incidentsInTimeRange: Int = 0,
    val maxChartValue: Int = 0
)
