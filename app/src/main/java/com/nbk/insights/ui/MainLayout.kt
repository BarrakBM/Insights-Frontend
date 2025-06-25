package com.nbk.insights.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.nbk.insights.ui.composables.AppHeader
import com.nbk.insights.ui.composables.BottomNavigationBar
import com.nbk.insights.ui.theme.LightBg

@Composable
fun MainLayout(
    selectedTab: String,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = { AppHeader() },
        bottomBar = { BottomNavigationBar(selectedTab = selectedTab, navController = navController) },
        containerColor = LightBg,
        content = content
    )
}