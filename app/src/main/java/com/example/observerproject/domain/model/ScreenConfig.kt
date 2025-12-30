package com.example.observerproject.domain.model

sealed class ScreenConfig(
    val name: String,
    val feature: String,
    val module: String,
    val errorCodePrefix: String
) {
    data object Home : ScreenConfig(
        name = "Home",
        feature = "home_incidents",
        module = "home",
        errorCodePrefix = "HOME"
    )

    data object Detail : ScreenConfig(
        name = "Detail",
        feature = "detail_incidents",
        module = "detail",
        errorCodePrefix = "DETAIL"
    )

    data object Dashboard : ScreenConfig(
        name = "Dashboard",
        feature = "dashboard_incidents",
        module = "dashboard",
        errorCodePrefix = "DASHBOARD"
    )
}

