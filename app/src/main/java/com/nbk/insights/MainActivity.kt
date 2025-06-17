package com.nbk.insights

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.safeDrawing.asPaddingValues())
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