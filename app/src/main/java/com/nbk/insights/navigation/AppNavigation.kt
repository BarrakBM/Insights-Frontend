package com.nbk.insights.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nbk.insights.ui.screens.HomeScreen
import com.nbk.insights.ui.screens.LoginScreen
import com.nbk.insights.ui.screens.InsightsScreen
import com.nbk.insights.ui.screens.AllTransactionsScreen
import com.nbk.insights.viewmodels.AccountsViewModel
import com.nbk.insights.viewmodels.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    accountsViewModel: AccountsViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(viewModel = authViewModel, navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                onViewAllCards = { /* TODO: Implement navigation to View All Cards Screen */ },
                onViewAllTransactions = {
                    navController.navigate(Screen.AllTransactions.route)
                },
                authViewModel = authViewModel,
                accountsViewModel = accountsViewModel
            )
        }

        composable(Screen.Insights.route) {
            InsightsScreen(navController = navController)
        }
        composable(Screen.AllTransactions.route) {
            AllTransactionsScreen(navController = navController)
        }
    }
}
