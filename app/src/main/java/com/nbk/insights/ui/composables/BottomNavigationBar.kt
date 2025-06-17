package com.nbk.insights.ui.composables

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nbk.insights.data.dtos.BottomNavBarDTO
import com.nbk.insights.ui.theme.InsightsTheme

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    navController: NavController
) {
    val items = listOf(
        BottomNavBarDTO("Home", Icons.Default.Home, "home"),
        BottomNavBarDTO("Cards", Icons.Default.CreditCard, "cards"),
        BottomNavBarDTO("Insights", Icons.Default.PieChart, "insights"),
        BottomNavBarDTO("Settings", Icons.Default.Settings, "settings")
    )

    NavigationBar(
        modifier = Modifier
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.title,
                        tint = if (selectedTab == item.title) Color(0xFF1E3A8A) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selectedTab == item.title) Color(0xFF1E3A8A) else Color.Gray
                    )
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
                    selectedIconColor = Color(0xFF1E3A8A),
                    selectedTextColor = Color(0xFF1E3A8A),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFF1E3A8A).copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    InsightsTheme {
        BottomNavigationBar(
            selectedTab = "Home",
            navController = rememberNavController()
        )
    }
}