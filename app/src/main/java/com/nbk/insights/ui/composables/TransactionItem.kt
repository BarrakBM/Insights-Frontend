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
import com.nbk.insights.data.dtos.TransactionResponse
import com.nbk.insights.data.dtos.MCC
import com.nbk.insights.data.dtos.TransactionType
import com.nbk.insights.ui.theme.InsightsTheme
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TransactionItem(transaction: TransactionResponse) {
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(getColorForCategory(transaction.mcc.category).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        getIconForCategory(transaction.mcc.category),
                        contentDescription = transaction.mcc.category,
                        tint = getColorForCategory(transaction.mcc.category),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = transaction.mcc.category,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    // Show subcategory if available
                    transaction.mcc.subCategory?.let { subCategory ->
                        Text(
                            text = subCategory,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF6B7280)
                        )
                    }

                    Text(
                        text = formatDateTime(transaction.createdAt),
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatAmount(transaction.amount, transaction.transactionType),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = determineTransactionColor(transaction)
                )

                // Show transaction type indicator
                transaction.transactionType?.let { type ->
                    Text(
                        text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 10.sp,
                        color = Color(0xFF9CA3AF),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Helper function to format LocalDateTime properly
fun formatDateTime(dateTime: String): String {
    return try {
        val parsedDateTime = LocalDateTime.parse(dateTime)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
        parsedDateTime.format(formatter)
    } catch (e: Exception) {
        // Fallback for different date formats
        try {
            // Try parsing ISO format with different patterns
            val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val parsedDateTime = LocalDateTime.parse(dateTime, isoFormatter)
            val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
            parsedDateTime.format(displayFormatter)
        } catch (e2: Exception) {
            // If all parsing fails, return the original string
            dateTime
        }
    }
}

// Helper function to format amount with proper sign and currency
fun formatAmount(amount: BigDecimal, transactionType: TransactionType?): String {
    val absAmount = amount.abs()
    val sign = when (transactionType) {
        TransactionType.CREDIT -> "+"
        TransactionType.DEBIT -> "-"
        else -> if (amount >= BigDecimal.ZERO) "+" else "-"
    }
    return "${sign}KD ${String.format("%.3f", absAmount)}"
}

// Helper function to determine transaction color based on type
fun determineTransactionColor(transaction: TransactionResponse): Color {
    return when (transaction.transactionType) {
        TransactionType.CREDIT -> Color(0xFF10B981) // Green for credit/income
        TransactionType.DEBIT -> Color(0xFFEF4444)  // Red for debit/expense
        else -> Color(0xFF6B7280) // Gray for unknown
    }
}

// Enhanced helper functions to map categories to icons and colors
fun getIconForCategory(category: String?): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category?.lowercase()) {
        "dining", "food", "restaurant", "fast food" -> Icons.Default.RestaurantMenu
        "shopping", "retail", "grocery" -> Icons.Default.ShoppingCart
        "transport", "transportation", "fuel", "gas" -> Icons.Default.DirectionsCar
        "income", "salary", "deposit" -> Icons.AutoMirrored.Filled.TrendingUp
        "entertainment", "movies", "gaming" -> Icons.Default.Movie
        "bills", "utilities", "phone", "internet" -> Icons.Default.Receipt
        "healthcare", "medical", "pharmacy" -> Icons.Default.LocalHospital
        "education", "books", "school" -> Icons.Default.School
        "travel", "hotel", "airline" -> Icons.Default.Flight
        "insurance" -> Icons.Default.Security
        "investment", "stocks" -> Icons.Default.TrendingUp
        "cash", "atm" -> Icons.Default.Money
        "transfer" -> Icons.Default.SwapHoriz
        else -> Icons.Default.AccountBalance
    }
}

fun getColorForCategory(category: String?): Color {
    return when (category?.lowercase()) {
        "dining", "food", "restaurant", "fast food" -> Color(0xFFEF4444)
        "shopping", "retail", "grocery" -> Color(0xFF8B5CF6)
        "transport", "transportation", "fuel", "gas" -> Color(0xFF3B82F6)
        "income", "salary", "deposit" -> Color(0xFF10B981)
        "entertainment", "movies", "gaming" -> Color(0xFFF59E0B)
        "bills", "utilities", "phone", "internet" -> Color(0xFF6B7280)
        "healthcare", "medical", "pharmacy" -> Color(0xFFDC2626)
        "education", "books", "school" -> Color(0xFF7C3AED)
        "travel", "hotel", "airline" -> Color(0xFF0891B2)
        "insurance" -> Color(0xFF059669)
        "investment", "stocks" -> Color(0xFF1D4ED8)
        "cash", "atm" -> Color(0xFF65A30D)
        "transfer" -> Color(0xFF9333EA)
        else -> Color(0xFF6366F1)
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionItemPreview() {
    InsightsTheme {
        TransactionItem(
            transaction = TransactionResponse(
                id = 1L,
                sourceAccountId = 123L,
                destinationAccountId = null,
                amount = BigDecimal("4.75"),
                transactionType = TransactionType.DEBIT,
                mcc = MCC(category = "Dining", subCategory = "Fast Food"),
                createdAt = LocalDateTime.now().toString()
            )
        )
    }
}

@Preview(showBackground = true, name = "Income Transaction")
@Composable
fun TransactionItemIncomePreview() {
    InsightsTheme {
        TransactionItem(
            transaction = TransactionResponse(
                id = 2L,
                sourceAccountId = null,
                destinationAccountId = 123L,
                amount = BigDecimal("2500.000"),
                transactionType = TransactionType.CREDIT,
                mcc = MCC(category = "Income", subCategory = "Salary"),
                createdAt = LocalDateTime.now().minusDays(1).toString()
            )
        )
    }
}

@Preview(showBackground = true, name = "Transaction with Long Subcategory")
@Composable
fun TransactionItemLongSubcategoryPreview() {
    InsightsTheme {
        TransactionItem(
            transaction = TransactionResponse(
                id = 3L,
                sourceAccountId = 123L,
                destinationAccountId = null,
                amount = BigDecimal("125.500"),
                transactionType = TransactionType.DEBIT,
                mcc = MCC(category = "Shopping", subCategory = "Electronics & Appliances"),
                createdAt = LocalDateTime.now().minusHours(3).toString()
            )
        )
    }
}