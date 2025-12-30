package com.example.observerproject.ui.dashboard

enum class TimeFilter(val minutes: Int, val label: String) {
    LAST_15_MINUTES(15, "15 min"),
    LAST_30_MINUTES(30, "30 min"),
    LAST_60_MINUTES(60, "60 min"),
    LAST_90_MINUTES(90, "90 min")
}

data class TimeChartDataPoint(
    val minuteOffset: Int,
    val count: Int,
    val homeCount: Int = 0,
    val detailCount: Int = 0,
    val dashboardCount: Int = 0
)
