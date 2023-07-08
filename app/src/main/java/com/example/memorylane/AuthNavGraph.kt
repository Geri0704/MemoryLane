package com.example.memorylane

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import java.security.AuthProvider

fun NavGraphBuilder.authNavGraph(navController: NavController){
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Login.route
    ){
         composable(route = AuthScreen.Login.route) {
             var authToken = remember{ mutableStateOf("") }
             LogInPage(
                 token = authToken,
                 onLoginSuccessful = {
                     navController.popBackStack()
                     navController.navigate(Graph.HOME)
                 },
                 onCreateAccountClick = {
                     navController.navigate(AuthScreen.CreateAccount.route)
                 }
             )
         }
        composable(route = AuthScreen.CreateAccount.route){
            AccountCreation()
        }
    }
}

sealed class AuthScreen(val route: String){
    object Login : AuthScreen(route = "LOGIN")
    object CreateAccount : AuthScreen(route = "CREATE_ACCOUNT")
}