package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.ui.theme.InsightsTheme
import com.nbk.insights.ui.theme.*

@Composable
fun RecentTransactionItem(transaction: RecentTransaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGray, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(transaction.iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    transaction.icon,
                    contentDescription = null,
                    tint = transaction.iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = transaction.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "${transaction.category} • ${transaction.time}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        Text(
            text = transaction.amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Error
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecentTransactionItemPreview() {
    InsightsTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RecentTransactionItem(
                RecentTransaction(
                    "Starbucks Coffee",
                    "Dining",
                    "-KD 4.50",
                    "Today 2:30 PM",
                    Icons.Default.Restaurant,
                    CategoryDining
                )
            )
            RecentTransactionItem(
                RecentTransaction(
                    "Amazon Purchase",
                    "Shopping",
                    "-KD 67.20",
                    "Yesterday 4:15 PM",
                    Icons.Default.ShoppingBag,
                    CategoryShopping
                )
            )
        }
    }
}