package com.example.memorylane

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.authNavGraph(navController: NavController, context: android.content.Context){
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Login.route
    ){
         composable(route = AuthScreen.Login.route) {
             LogInPage(
                 onLoginSuccessful = {
                     navController.popBackStack()
                     navController.navigate(Graph.HOME)
                 },
                 onCreateAccountClick = {
                     navController.navigate(AuthScreen.CreateAccount.route)
                 },
                 context = context
             )
         }
        composable(route = AuthScreen.CreateAccount.route){
            AccountCreation(
                onCreateAccountSuccessful = {
                    navController.popBackStack()
                    navController.navigate(Graph.HOME)
                },
                onBackClick = {
                    navController.navigate(AuthScreen.Login.route)
                },
                context = context
            )
        }
    }
}

sealed class AuthScreen(val route: String){
    object Login : AuthScreen(route = "LOGIN")
    object CreateAccount : AuthScreen(route = "CREATE_ACCOUNT")
}