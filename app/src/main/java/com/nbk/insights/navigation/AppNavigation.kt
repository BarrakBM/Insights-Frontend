package com.nbk.insights.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nbk.insights.data.tempfunctions.getBankCards
import com.nbk.insights.ui.composables.CardInsightContent
import com.nbk.insights.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Home2.route) {
            HomeScreen2(navController)
        }
        composable(Screen.AllTransactions.route) {
            AllTransactionsScreen(navController)
        }
        composable(Screen.AccountTransactions.route) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getString("accountId")?.toLongOrNull()
            accountId?.let {
                // Use the existing AllTransactionsScreen but pass the accountId
                AllTransactionsScreen(navController, it)
            }
        }
        composable(Screen.Insights.route) {
            InsightsScreen(navController)
        }
        composable(Screen.Insights2.route) {
            InsightsScreen2(navController)
        }
        composable(Screen.Notifications.route) {
            NotificationScreen(navController)
        }
        composable(Screen.RecurringPayments.route) {
            RecurringPaymentsScreen(navController)
        }
        composable("card_insights/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            val card = getBankCards().find { it.lastFourDigits == cardId }

            card?.let {
                CardInsightContent(card = it, onDismiss = {
                    navController.popBackStack()
                })
            }
        }
    }
}