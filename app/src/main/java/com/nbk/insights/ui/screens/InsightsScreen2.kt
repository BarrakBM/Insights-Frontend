package com.nbk.insights.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import com.nbk.insights.ui.theme.*

@Composable
fun InsightsScreen2(navController: NavController, paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp)
    ) {
        item { AccountCard() }
        item { MoneyFlowSection() }
        item { FinancialInsights() }
        item { BudgetProgress() }
        item { SmartRecommendations() }
        item { RecurringTransactions() }
    }
    }

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
fun BudgetProgress() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Budget Progress",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        BudgetItem(
            icon = Icons.Default.Restaurant,
            category = "Dining",
            spent = 320f,
            total = 400f,
            color = WarningAmber,
            status = "80% used - Watch your spending"
        )

        BudgetItem(
            icon = Icons.Default.ShoppingCart,
            category = "Groceries",
            spent = 280f,
            total = 500f,
            color = SuccessGreen,
            status = "56% used - On track"
        )

        BudgetItem(
            icon = Icons.Default.SportsEsports,
            category = "Entertainment",
            spent = 85f,
            total = 200f,
            color = PrimaryBlue,
            status = "42% used - Doing great"
        )
    }
}

@Composable
fun BudgetItem(
    icon: ImageVector,
    category: String,
    spent: Float,
    total: Float,
    color: Color,
    status: String
) {
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
                        category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
                Text(
                    "${spent.toInt()} / ${total.toInt()}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            LinearProgressIndicator(
                progress = { spent / total },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
            Text(
                status,
                fontSize = 12.sp,
                color = color
            )
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