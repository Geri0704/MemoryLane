package com.example.memorylane

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            Base()
        }
        composable(route = BottomBarScreen.Notifications.route) {
            //Add notifications composable once we make the screen
        }
        composable(route = BottomBarScreen.Settings.route) {
            SettingsPage()
        }
        composable(route = BottomBarScreen.Analytics.route) {
            AnalyticsPageParent()
        }
    }
}
