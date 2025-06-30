package com.nbk.insights.ui.composables

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.BottomNavBarDTO
import com.nbk.insights.ui.theme.NBKBlue
import com.nbk.insights.ui.theme.NBKBlueAlpha10

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    navController: NavController
) {
    val items = listOf(
        BottomNavBarDTO("Home", Icons.Default.Home, "home2"),
        BottomNavBarDTO("Insights", Icons.Default.PieChart, "insights2"),
        BottomNavBarDTO("Explore", Icons.Default.Explore, "explore"), // New Explore tab
        BottomNavBarDTO("Recurring", Icons.Default.Refresh, "recurring_payments"),
    )

    NavigationBar(
        modifier = Modifier
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        containerColor = Color.White,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(item.icon, contentDescription = item.title)
                },
                label = {
                    Text(text = item.title)
                },
                selected = selectedTab == item.title,
                onClick = {
                    if (item.route != "cards" && item.route != "settings") {
                        navController.navigate(item.route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NBKBlue,
                    selectedTextColor = NBKBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = NBKBlueAlpha10
                )
            )
        }
    }
}