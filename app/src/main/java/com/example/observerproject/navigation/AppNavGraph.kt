package com.example.observerproject.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.observerproject.ui.dashboard.DashboardScreen
import com.example.observerproject.ui.dashboard.DashboardViewModel
import com.example.observerproject.ui.detail.DetailScreen
import com.example.observerproject.ui.detail.DetailViewModel
import com.example.observerproject.ui.home.HomeScreen
import com.example.observerproject.ui.home.HomeViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = AppDestination.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = AppDestination.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToDetail = {
                    navController.navigate(AppDestination.Detail.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(AppDestination.Dashboard.route) {
                        // Avoid building up a large stack
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = AppDestination.Detail.route) {
            val viewModel: DetailViewModel = hiltViewModel()
            DetailScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(AppDestination.Dashboard.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToHome = {
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = AppDestination.Dashboard.route) {
            val viewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
