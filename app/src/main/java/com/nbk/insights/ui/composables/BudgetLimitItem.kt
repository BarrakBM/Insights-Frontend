package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun BudgetLimitItem(
    budget: BudgetLimit,
    onClick: () -> Unit = {} // Added click callback
) {
    val progress = budget.spent / budget.limit
    val remaining = budget.limit - budget.spent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Make the whole item clickable
            .padding(vertical = 4.dp) // Add some padding for better touch target
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(budget.color.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        budget.icon,
                        contentDescription = null,
                        tint = budget.color,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = budget.category,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "KD ${budget.spent.toInt()} / KD ${budget.limit.toInt()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (budget.isOverBudget) Color(0xFFEF4444) else Color.Black
                )
                if (budget.isOverBudget) {
                    Text(
                        text = "Over budget",
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444)
                    )
                } else if (budget.isNearLimit) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Near limit!",
                            fontSize = 12.sp,
                            color = Color(0xFFF59E0B)
                        )
                    }
                } else {
                    Text(
                        text = "KD ${remaining.toInt()} remaining",
                        fontSize = 12.sp,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = progress.coerceAtMost(1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                budget.isOverBudget -> Color(0xFFEF4444)
                budget.isNearLimit -> Color(0xFFF59E0B)
                else -> budget.color
            },
            trackColor = Color(0xFFF3F4F6)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetLimitItemPreview() {
    InsightsTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BudgetLimitItem(
                BudgetLimit(
                    "Dining",
                    450f,
                    400f,
                    Color(0xFFEF4444),
                    Icons.Default.Restaurant,
                    isOverBudget = true
                ),
                onClick = { }
            )
            BudgetLimitItem(
                BudgetLimit(
                    "Shopping",
                    680f,
                    800f,
                    Color(0xFF3B82F6),
                    Icons.Default.ShoppingBag
                ),
                onClick = { }
            )
        }
    }
}