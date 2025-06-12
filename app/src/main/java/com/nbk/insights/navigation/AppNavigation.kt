package com.nbk.insights.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nbk.insights.ui.screens.HomeScreen
import com.nbk.insights.ui.screens.LoginScreen
import com.nbk.insights.viewmodels.AuthViewModel

@Composable
fun AppNavigation(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(viewModel = authViewModel, navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(viewModel = authViewModel, navController = navController)
        }
//        composable(Screen.Register.route) {
//            RegisterScreen(viewModel = viewModel, navController = navController)
//        }
    }
}