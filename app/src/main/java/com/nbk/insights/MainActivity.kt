package com.nbk.insights

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nbk.insights.navigation.AppNavigation
import com.nbk.insights.navigation.Screen
import com.nbk.insights.ui.MainLayout
import com.nbk.insights.ui.theme.InsightsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color(0xFF1E3A8A).toArgb()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            InsightsTheme {
                InsightsApp()
            }
        }
    }
}

@Composable
fun InsightsApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val routesWithMainLayout = listOf(
        Screen.Home2.route,
        Screen.Insights2.route,
        Screen.RecurringPayments.route
    )

    val showMainLayout = currentRoute in routesWithMainLayout

    if (showMainLayout) {
        MainLayout(
            selectedTab = when (currentRoute) {
                Screen.Home2.route -> "Home"
                Screen.Insights2.route -> "Insights"
                Screen.RecurringPayments.route -> "Recurring"
                else -> "Home"
            },
            navController = navController
        ) { paddingValues ->
            AppNavigation(navController, paddingValues)
        }
    } else {
        // For other routes (Login, etc.), show NavHost without MainLayout
        AppNavigation(navController, PaddingValues())
    }
}