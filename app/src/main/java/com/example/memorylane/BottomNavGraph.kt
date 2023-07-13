package com.example.memorylane

import NotificationInboxPage
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
            NotificationInboxPage()
        }
        composable(route = BottomBarScreen.Settings.route) {
            SettingsPage()
        }
        composable(route = BottomBarScreen.Analytics.route) {
            AnalyticsPageParent()
        }
    }
}
