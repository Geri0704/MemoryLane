package com.example.memorylane

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.material.icons.outlined.Info

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Outlined.Home
    )
    object Notifications : BottomBarScreen(
        route = "notifications",
        title = "Notifications",
        icon = Icons.Outlined.Notifications
    )
    object Settings : BottomBarScreen(
        route = "settings",
        title = "Settings",
        icon = Icons.Outlined.Settings
    )
    object Analytics : BottomBarScreen(
        route = "analytics",
        title = "Analytics",
        icon = Icons.Outlined.Info
    )
}
