package com.example.observerproject.navigation

sealed class AppDestination(val route: String) {

    data object Home : AppDestination("home")

    data object Detail : AppDestination("detail")

    data object Dashboard : AppDestination("dashboard")
}

