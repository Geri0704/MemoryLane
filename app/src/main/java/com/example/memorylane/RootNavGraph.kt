package com.example.memorylane

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun RootNavigationGraph(navController: NavHostController){
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
    val authToken = sharedPref.getString("authToken", null)

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = if (authToken == null ) Graph.AUTHENTICATION else Graph.HOME
    ){
        authNavGraph(navController = navController, context)
        composable(route = Graph.HOME){
            mainScreen()
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
}
