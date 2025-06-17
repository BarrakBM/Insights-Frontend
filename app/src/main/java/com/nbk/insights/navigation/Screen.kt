package com.nbk.insights.navigation

sealed class Screen(val route: String){
    object Login : Screen("login")
    object Home : Screen("home")
    object Insights : Screen("insights")
    object AllTransactions : Screen("all_transactions")
}
