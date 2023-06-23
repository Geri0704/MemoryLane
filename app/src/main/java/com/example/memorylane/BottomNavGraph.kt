package com.example.memorylane

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun BottomNavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ){
        composable(route = BottomBarScreen.Home.route){
            Base(onNavigateToJournal = {

                navController.navigate("journal") })
        }
        composable(route = BottomBarScreen.Notifications.route){
            //Add notifications composable once we make the screen
        }
        composable(route = BottomBarScreen.Settings.route){
            SettingsPage()
        }
        composable(route = "journal"){
            JournalPage()
        }
    }
}