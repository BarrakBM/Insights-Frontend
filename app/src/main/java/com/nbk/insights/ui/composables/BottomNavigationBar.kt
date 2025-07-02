package com.nbk.insights.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.BottomNavBarDTO
import com.nbk.insights.ui.theme.NBKBlue
import com.nbk.insights.ui.theme.NBKBlueAlpha10
import com.nbk.insights.ui.theme.TextSecondary

data class EnhancedBottomNavItem(
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    navController: NavController
) {
    val items = listOf(
        EnhancedBottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, "home"),
        EnhancedBottomNavItem("Insights", Icons.Filled.Analytics, Icons.Outlined.Analytics, "insights"),
        EnhancedBottomNavItem("Explore", Icons.Filled.Explore, Icons.Outlined.Explore, "explore"),
    )

    NavigationBar(
        modifier = Modifier
            .height(72.dp) // Slightly reduced for better proportion
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                clip = false
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        containerColor = Color.White,
        tonalElevation = 0.dp, // Remove default elevation as we're using shadow
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        items.forEach { item ->
            val selected = selectedTab == item.title

            // Animate selection state
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.1f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "scale"
            )

            val iconColor by animateColorAsState(
                targetValue = if (selected) NBKBlue else TextSecondary,
                label = "iconColor"
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.filledIcon else item.outlinedIcon,
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(if (selected) 26.dp else 24.dp)
                            .scale(scale),
                        tint = iconColor
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = if (selected) 13.sp else 12.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = iconColor,
                        modifier = Modifier.scale(if (selected) 1.05f else 1f)
                    )
                },
                selected = selected,
                onClick = {
                    if (selectedTab != item.title) {
                        navController.navigate(item.route) {
                            popUpTo("home") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NBKBlue,
                    selectedTextColor = NBKBlue,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = NBKBlueAlpha10
                )
            )
        }
    }
}