package com.nbk.insights

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.nbk.insights.navigation.AppNavigation
import com.nbk.insights.ui.theme.InsightsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color(0xFF1E3A8A).toArgb()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false

        setContent {
            InsightsTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
