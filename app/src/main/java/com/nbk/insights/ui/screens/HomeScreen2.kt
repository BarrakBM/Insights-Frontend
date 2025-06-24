// HomeScreen.kt
package com.nbk.insights.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.TransactionResponse
import com.nbk.insights.navigation.Screen
import com.nbk.insights.ui.composables.BalanceCard
import com.nbk.insights.ui.composables.BottomNavigationBar
import com.nbk.insights.ui.composables.GreetingSection
import com.nbk.insights.ui.composables.SpendingViewAllChart
import com.nbk.insights.ui.composables.TransactionItem
import com.nbk.insights.ui.theme.NBKBlue
import com.nbk.insights.ui.theme.PurpleGrey40
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.AccountsViewModel
import com.nbk.insights.viewmodels.AuthViewModel
import com.nbk.insights.viewmodels.TransactionsViewModel
import java.math.BigDecimal
import kotlin.math.min

// Color definitions
val PrimaryBlue = Color(0xFF0D47A1)
val SuccessGreen = Color(0xFF43A047)
val WarningAmber = Color(0xFFFFC107)
val LightBg = Color(0xFFF5F5F5)
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)

@Composable
fun HomeScreen2(navController: NavController) {
    /* ── view-models ─────────────────────────────── */
    val ctx = LocalContext.current
    val authVM: AuthViewModel = viewModel(factory = remember { AppInitializer.provideAuthViewModelFactory(ctx) })
    val accountsVM: AccountsViewModel = viewModel(factory = remember { AppInitializer.provideAccountsViewModelFactory(ctx) })
    val txVM: TransactionsViewModel = viewModel(factory = remember { AppInitializer.provideTransactionsViewModelFactory(ctx) })

    /* ── state ───────────────────────────────────── */
    val recentTxs          by txVM.userTransactions
    val totalTx            = recentTxs?.size ?: 0

    var shownCount by remember { mutableIntStateOf(4) }
    val visibleTx   = recentTxs.orEmpty().take(shownCount) // how many tx currently shown

    val firstName    = authVM.user.value?.fullName?.split(" ")?.firstOrNull() ?: "Guest"
    val totalBalance = accountsVM.totalBalance.value?.totalBalance ?: BigDecimal.ZERO

    LaunchedEffect(Unit) {
        accountsVM.fetchUserAccounts()
        accountsVM.fetchTotalBalance()
        txVM.fetchUserTransactions()
    }

    Scaffold(
        topBar = { AppHeader() },
        bottomBar = { BottomNavigationBar(selectedTab = "Home2", navController = navController) },
        containerColor = LightBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { GreetingSection(firstName) }
            item { BalanceCard(balance = "KD $totalBalance") }
            item { SpendingViewAllChart() }
            item { QuickInsights() }
            item {
                RecentTransactions(
                    navController = navController,
                    visibleTx = visibleTx,
                    totalTx = totalTx,
                    shownCount = shownCount,
                    onShownCountChange = { shownCount = it }
                )
            }

        }
    }
}

@Composable
fun AppHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    "Insights+",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = TextSecondary
                )
            }
        }
    }
}

@Composable
fun SpendingChart() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Spending Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            // Placeholder for chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .background(LightBg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Chart Placeholder", color = TextSecondary)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MoneyFlowItem(
                    icon = Icons.Default.ArrowDownward,
                    label = "Money In",
                    amount = "$5,240",
                    color = SuccessGreen
                )
                MoneyFlowItem(
                    icon = Icons.Default.ArrowUpward,
                    label = "Money Out",
                    amount = "$3,180",
                    color = Color.Red
                )
                MoneyFlowItem(
                    icon = Icons.Default.Savings,
                    label = "Saved",
                    amount = "$2,060",
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Composable
fun MoneyFlowItem(
    icon: ImageVector,
    label: String,
    amount: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                label,
                fontSize = 14.sp,
                color = color
            )
        }
        Text(
            amount,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
fun QuickInsights() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Quick Insights",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                InsightCard(
                    icon = Icons.Default.Lightbulb,
                    title = "Great savings this month!",
                    description = "You've saved 15% more than your target. Keep it up!",
                    color = SuccessGreen
                )
            }
            item {
                InsightCard(
                    icon = Icons.Default.Warning,
                    title = "Dining budget alert",
                    description = "You've spent 80% of your dining budget this month.",
                    color = WarningAmber
                )
            }
            item {
                InsightCard(
                    icon = Icons.Default.TrendingUp,
                    title = "Investment opportunity",
                    description = "Consider increasing your investment portfolio by 5%.",
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Composable
fun InsightCard(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 0.dp,
            color = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawColoredShadow(color, 0.1f)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun RecentTransactions(
    navController: NavController,
    visibleTx: List<TransactionResponse>,
    totalTx: Int,
    shownCount: Int,
    onShownCountChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                TextButton(onClick = { navController.navigate(Screen.AllTransactions.route) }) {
                    Text("View All", color = NBKBlue)
                }
            }

            // Transaction list
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                visibleTx.forEach { tx ->
                    TransactionItem(transaction = tx)
                }
            }

            // Show More / Show Less
            if (totalTx > 4) {
                val showMore = shownCount < totalTx
                val showLess = shownCount > 4

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (showLess) {
                        Text(
                            text = "Show Less",
                            fontWeight = FontWeight.Medium,
                            color = PurpleGrey40,
                            modifier = Modifier.clickable { onShownCountChange(4) }
                        )
                    }

                    if (showLess && showMore) Spacer(Modifier.width(24.dp))

                    if (showMore) {
                        Text(
                            text = "Show More",
                            fontWeight = FontWeight.Medium,
                            color = NBKBlue,
                            modifier = Modifier.clickable {
                                onShownCountChange(min(shownCount + 4, totalTx))
                            }
                        )
                    }
                }
            }
        }
    }
}


//@Composable
//fun TransactionItem(
//    icon: ImageVector,
//    title: String,
//    date: String,
//    amount: String,
//    isIncome: Boolean
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape)
//                    .background(
//                        if (isIncome) SuccessGreen.copy(alpha = 0.1f)
//                        else Color.Red.copy(alpha = 0.1f)
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    icon,
//                    contentDescription = null,
//                    tint = if (isIncome) SuccessGreen else Color.Red,
//                    modifier = Modifier.size(20.dp)
//                )
//            }
//            Column {
//                Text(
//                    title,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Medium,
//                    color = TextPrimary
//                )
//                Text(
//                    date,
//                    fontSize = 12.sp,
//                    color = TextSecondary
//                )
//            }
//        }
//        Text(
//            amount,
//            fontSize = 14.sp,
//            fontWeight = FontWeight.SemiBold,
//            color = if (isIncome) SuccessGreen else Color.Red
//        )
//    }
//}

@Composable
fun BottomNavigation(
    selectedIndex: Int,
    onNavigateToInsights: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = {},
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = onNavigateToInsights,
            icon = { Icon(Icons.Default.Analytics, contentDescription = "Insights") },
            label = { Text("Insights", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = {},
            icon = { Icon(Icons.Default.Repeat, contentDescription = "Recurring") },
            label = { Text("Recurring", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}

// Extension function for colored shadow
fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.12f,
    borderRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    blurRadius: Dp = 0.dp
) = this.then(Modifier)