package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.ui.theme.InsightsTheme

@Composable
fun InsightsTabContent(
    selectedTab: Int,
    onViewAllTransactions: () -> Unit
) {
    when (selectedTab) {
        0 -> {
            // Show Insights Tab
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Spending Insights",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Sample spending categories
                val categories = listOf(
                    "Dining" to 45f,
                    "Shopping" to 30f,
                    "Transportation" to 15f,
                    "Entertainment" to 10f
                )

                categories.forEach { (category, percentage) ->
                    SpendingCategoryBar(
                        category = category,
                        percentage = percentage
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "💡 You spent 23% more on dining this month compared to last month.",
                    fontSize = 14.sp,
                    color = Color(0xFF7C3AED),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF7C3AED).copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                )
            }
        }
        1 -> {
            // Transactions Tab
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onViewAllTransactions,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF3F4F6)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = "View All Transactions",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View All Transactions",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        2 -> {
            // Budget Planning Tab
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { /* TODO: Navigate to budget planning */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E3A8A)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = "Start Budget Planning",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Budget Planning",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SpendingCategoryBar(
    category: String,
    percentage: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${percentage.toInt()}%",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFF3F4F6))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        when (category) {
                            "Dining" -> Color(0xFFEF4444)
                            "Shopping" -> Color(0xFF8B5CF6)
                            "Transportation" -> Color(0xFF06B6D4)
                            "Entertainment" -> Color(0xFF10B981)
                            else -> Color(0xFF6B7280)
                        }
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InsightsTabContentPreview() {
    InsightsTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            InsightsTabContent(
                selectedTab = 0,
                onViewAllTransactions = { }
            )
        }
    }
}

@Preview(showBackground = true, name = "Transactions Tab")
@Composable
fun InsightsTabContentTransactionsPreview() {
    InsightsTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            InsightsTabContent(
                selectedTab = 1,
                onViewAllTransactions = { }
            )
        }
    }
}

@Preview(showBackground = true, name = "Budget Planning Tab")
@Composable
fun InsightsTabContentBudgetPreview() {
    InsightsTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            InsightsTabContent(
                selectedTab = 2,
                onViewAllTransactions = { }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpendingCategoryBarPreview() {
    InsightsTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpendingCategoryBar(category = "Dining", percentage = 45f)
            SpendingCategoryBar(category = "Shopping", percentage = 30f)
            SpendingCategoryBar(category = "Transportation", percentage = 15f)
        }
    }
}