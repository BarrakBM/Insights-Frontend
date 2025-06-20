package com.nbk.insights.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Insights : Screen("insights")
    data object AllTransactions : Screen("all_transactions")
    data object Notifications : Screen("notifications")
}
