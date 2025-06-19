package com.nbk.insights

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.nbk.insights.navigation.AppNavigation
import com.nbk.insights.ui.theme.InsightsTheme
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.AccountsViewModel
import com.nbk.insights.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color(0xFF1E3A8A).toArgb()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            isAppearanceLightStatusBars = false // false = white icons
        }

        setContent {
            InsightsTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel(
                    factory = AppInitializer.provideAuthViewModelFactory(applicationContext)
                )
                val accountsViewModel: AccountsViewModel = viewModel(
                    factory = AppInitializer.provideAccountsViewModelFactory(applicationContext)
                )

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        accountsViewModel = accountsViewModel
                    )
                }
            }
        }
    }
}


