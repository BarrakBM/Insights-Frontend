package com.nbk.insights.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Insights : Screen("insights")
    data object Explore : Screen("explore") // New Explore screen
    data object AllTransactions : Screen("all_transactions")
    data object AccountTransactions : Screen("account_transactions/{accountId}") {
        fun createRoute(accountId: Long) = "account_transactions/$accountId"
    }
    data object Notifications : Screen("notifications")
    data object RecurringPayments : Screen("recurring_payments")
    data object BudgetManagement : Screen("budget_management")

}