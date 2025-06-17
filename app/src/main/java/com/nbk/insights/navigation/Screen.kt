package com.nbk.insights.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Insights : Screen("insights")
    object AllTransactions : Screen("all_transactions")
}
