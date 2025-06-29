package com.nbk.insights.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.*
import com.nbk.insights.ui.theme.*
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.AccountsViewModel
import java.math.RoundingMode
import androidx.compose.runtime.getValue

@Composable
fun InsightsScreen2(navController: NavController, paddingValues: PaddingValues) {
    val context = LocalContext.current
    val accountsViewModel: AccountsViewModel = viewModel(
        factory = remember { AppInitializer.provideAccountsViewModelFactory(context) }
    )

    // Fetch budget data on load
    LaunchedEffect(Unit) {
        accountsViewModel.fetchBudgetAdherence()
        accountsViewModel.fetchSpendingTrends()
    }

    val budgetAdherence by accountsViewModel.budgetAdherence
    val spendingTrends by accountsViewModel.spendingTrends
    val isLoading by accountsViewModel.isLoading

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { AccountCard() }
        item { MoneyFlowSection() }
        item { FinancialInsights() }

        // Updated Budget Progress with real data
        item {
            BudgetProgressWithData(
                budgetAdherence = budgetAdherence,
                isLoading = isLoading,
                onNavigateToBudget = {
                    navController.navigate("budget_management")
                }
            )
        }

        item { SmartRecommendations() }
        item { RecurringTransactions() }
    }
}

@Composable
fun BudgetProgressWithData(
    budgetAdherence: BudgetAdherenceResponse?,
    isLoading: Boolean,
    onNavigateToBudget: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Budget Progress",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            TextButton(onClick = onNavigateToBudget) {
                Text(
                    "Manage",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
            }
        }

        when {
            isLoading -> {
                // Loading state
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            budgetAdherence?.categoryAdherences?.isEmpty() != false -> {
                // Empty state
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = onNavigateToBudget
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = PrimaryBlue.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "No budgets set yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            "Tap here to start budgeting",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            else -> {
                // Show top 3 budget categories
                val topCategories = budgetAdherence.categoryAdherences
                    .sortedByDescending { it.percentageUsed }
                    .take(3)

                topCategories.forEach { categoryAdherence ->
                    BudgetItemFromData(categoryAdherence)
                }

                // Show "View All" if there are more than 3 categories
                if (budgetAdherence.categoryAdherences.size > 3) {
                    TextButton(
                        onClick = onNavigateToBudget,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "View all ${budgetAdherence.categoryAdherences.size} budgets →",
                            fontSize = 14.sp,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetItemFromData(categoryAdherence: CategoryAdherence) {
    val icon = getBudgetCategoryIcon(categoryAdherence.category)
    val color = getBudgetCategoryColor(categoryAdherence.adherenceLevel)
    val spent = categoryAdherence.spentAmount.toFloat()
    val total = categoryAdherence.budgetAmount.toFloat()
    val status = getBudgetStatusText(categoryAdherence)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        categoryAdherence.category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
                Text(
                    "KD ${categoryAdherence.spentAmount.setScale(3, RoundingMode.HALF_UP)} / " +
                            "KD ${categoryAdherence.budgetAmount.setScale(3, RoundingMode.HALF_UP)}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            LinearProgressIndicator(
                progress = { (categoryAdherence.percentageUsed / 100f).coerceIn(0.0, 1.0).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    status,
                    fontSize = 12.sp,
                    color = color
                )

                // Show trend if available
                if (categoryAdherence.spendingTrend != SpendingTrend.NO_DATA) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (categoryAdherence.spendingTrend) {
                                SpendingTrend.INCREASED -> Icons.Default.TrendingUp
                                SpendingTrend.DECREASED -> Icons.Default.TrendingDown
                                else -> Icons.Default.TrendingFlat
                            },
                            contentDescription = null,
                            tint = when (categoryAdherence.spendingTrend) {
                                SpendingTrend.INCREASED -> WarningAmber
                                SpendingTrend.DECREASED -> SuccessGreen
                                else -> TextSecondary
                            },
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            "${categoryAdherence.spendingChangePercentage.toInt()}% vs last month",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// Helper functions - renamed to avoid conflicts
private fun getBudgetCategoryIcon(category: String): ImageVector {
    return when (category.uppercase()) {
        "DINING" -> Icons.Default.Restaurant
        "GROCERIES", "FOOD_AND_GROCERIES" -> Icons.Default.ShoppingCart
        "ENTERTAINMENT" -> Icons.Default.SportsEsports
        "SHOPPING" -> Icons.Default.ShoppingBag
        "TRANSPORT", "TRANSPORTATION" -> Icons.Default.DirectionsCar
        "UTILITIES" -> Icons.Default.Bolt
        "HEALTHCARE" -> Icons.Default.LocalHospital
        else -> Icons.Default.Category
    }
}

private fun getBudgetCategoryColor(adherenceLevel: AdherenceLevel): Color {
    return when (adherenceLevel) {
        AdherenceLevel.EXCELLENT -> PrimaryBlue
        AdherenceLevel.GOOD -> SuccessGreen
        AdherenceLevel.WARNING -> WarningAmber
        AdherenceLevel.CRITICAL -> Color(0xFFFF6B6B)
        AdherenceLevel.EXCEEDED -> Color.Red
    }
}

private fun getBudgetStatusText(categoryAdherence: CategoryAdherence): String {
    val percentageUsed = categoryAdherence.percentageUsed.toInt()
    return when (categoryAdherence.adherenceLevel) {
        AdherenceLevel.EXCELLENT -> "$percentageUsed% used - Doing great"
        AdherenceLevel.GOOD -> "$percentageUsed% used - On track"
        AdherenceLevel.WARNING -> "$percentageUsed% used - Watch your spending"
        AdherenceLevel.CRITICAL -> "$percentageUsed% used - Near limit!"
        AdherenceLevel.EXCEEDED -> "Over budget by ${(percentageUsed - 100)}%"
    }
}

// Keep existing components unchanged
@Composable
fun AccountCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryBlue, Color(0xFF1976D2))
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                "Chase Checking",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                "****4567",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Current Balance",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        "$24,567.89",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MoneyFlowSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MoneyFlowCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.ArrowDownward,
            label = "Money In",
            amount = "$5,240",
            percentage = "+12%",
            color = SuccessGreen
        )
        MoneyFlowCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.ArrowUpward,
            label = "Money Out",
            amount = "$3,180",
            percentage = "+8%",
            color = Color.Red
        )
        MoneyFlowCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Savings,
            label = "Saved",
            amount = "$2,060",
            percentage = "+15%",
            color = PrimaryBlue,
            percentageColor = SuccessGreen
        )
    }
}

@Composable
fun MoneyFlowCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    amount: String,
    percentage: String,
    color: Color,
    percentageColor: Color = color
) {
    Card(
        modifier = modifier.shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                label,
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                percentage,
                fontSize = 12.sp,
                color = percentageColor
            )
        }
    }
}

@Composable
fun FinancialInsights() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Financial Insights",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        InsightDetailCard(
            icon = Icons.Default.TrendingUp,
            title = "Excellent Savings Rate",
            description = "You're saving 39% of your income this month - well above the recommended 20%!",
            progress = 0.39f,
            progressText = "39%",
            color = SuccessGreen
        )

        InsightDetailCard(
            icon = Icons.Default.PieChart,
            title = "Spending Pattern Analysis",
            description = "Your largest expense category is housing (45%), followed by food (22%).",
            actionText = "View detailed breakdown →",
            color = PrimaryBlue
        )
    }
}

@Composable
fun InsightDetailCard(
    icon: ImageVector,
    title: String,
    description: String,
    progress: Float? = null,
    progressText: String? = null,
    actionText: String? = null,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(80.dp)
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
                progress?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { it },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = color,
                            trackColor = Color.Gray.copy(alpha = 0.2f)
                        )
                        progressText?.let { text ->
                            Text(
                                text,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = color
                            )
                        }
                    }
                }
                actionText?.let {
                    Text(
                        it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun SmartRecommendations() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Smart Recommendations",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        RecommendationCard(
            icon = Icons.Default.Warning,
            title = "Reduce Dining Expenses",
            description = "You're spending 25% more on dining than last month. Consider meal planning to save $80-120.",
            actionText = "Set Dining Goal",
            color = WarningAmber
        )

        RecommendationCard(
            icon = Icons.Default.Lightbulb,
            title = "Increase Emergency Fund",
            description = "With your current savings rate, you could build a 6-month emergency fund in 8 months.",
            actionText = "Start Auto-Save",
            color = SuccessGreen
        )

        RecommendationCard(
            icon = Icons.Default.TrendingUp,
            title = "Investment Opportunity",
            description = "Based on your surplus, consider investing $500/month in index funds for long-term growth.",
            actionText = "Learn More",
            color = PrimaryBlue
        )
    }
}

@Composable
fun RecommendationCard(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
                TextButton(
                    onClick = {},
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        actionText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun RecurringTransactions() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recurring Transactions",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            TextButton(onClick = {}) {
                Text(
                    "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
            }
        }

        RecurringItem(
            icon = Icons.Default.Home,
            title = "Rent Payment",
            frequency = "Every 1st of month",
            amount = "-$1,200",
            nextDate = "Next: Jan 1",
            iconColor = Color.Red
        )

        RecurringItem(
            icon = Icons.Default.AttachMoney,
            title = "Salary Deposit",
            frequency = "Every 15th & 30th",
            amount = "+$2,500",
            nextDate = "Next: Dec 30",
            iconColor = SuccessGreen
        )

        RecurringItem(
            icon = Icons.Default.Savings,
            title = "Auto Savings",
            frequency = "Every 1st of month",
            amount = "-$500",
            nextDate = "Next: Jan 1",
            iconColor = PrimaryBlue
        )

        RecurringItem(
            icon = Icons.Default.Wifi,
            title = "Internet Bill",
            frequency = "Every 15th of month",
            amount = "-$89",
            nextDate = "Next: Jan 15",
            iconColor = WarningAmber
        )
    }
}

@Composable
fun RecurringItem(
    icon: ImageVector,
    title: String,
    frequency: String,
    amount: String,
    nextDate: String,
    iconColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        frequency,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    amount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (amount.startsWith("+")) SuccessGreen else if (iconColor == PrimaryBlue) PrimaryBlue else iconColor
                )
                Text(
                    nextDate,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}