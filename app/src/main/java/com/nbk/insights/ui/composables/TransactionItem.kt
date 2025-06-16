package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.nbk.insights.data.dtos.TransactionDTO
import com.nbk.insights.ui.theme.InsightsTheme


@Composable
fun TransactionItem(transaction: TransactionDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(transaction.iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        transaction.icon,
                        contentDescription = transaction.category,
                        tint = transaction.iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = transaction.date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = transaction.amount,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionItemPreview() {
    InsightsTheme {
        TransactionItem(
            transaction = TransactionDTO(
                title = "Starbucks",
                date = "Today, 9:15 AM",
                amount = "-KD 4.75",
                category = "Dining",
                icon = Icons.Default.RestaurantMenu,
                iconColor = Color(0xFFEF4444)
            )
        )
    }
}

@Preview(showBackground = true, name = "Income Transaction")
@Composable
fun TransactionItemIncomePreview() {
    InsightsTheme {
        TransactionItem(
            transaction = TransactionDTO(
                title = "Salary Deposit",
                date = "Yesterday, 10:00 AM",
                amount = "+KD 2,500.00",
                category = "Income",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconColor = Color(0xFF10B981),
                isIncome = true
            )
        )
    }
}
