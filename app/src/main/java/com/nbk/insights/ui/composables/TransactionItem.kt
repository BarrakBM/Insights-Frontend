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
import com.nbk.insights.ui.theme.*
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

                    transaction.mcc.subCategory?.let { subCategory ->
                        Text(
                            text = subCategory,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Gray500
                        )
                    }

                    Text(
                        text = formatDateTime(transaction.createdAt),
                        fontSize = 12.sp,
                        color = Gray400
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

                transaction.transactionType?.let { type ->
                    Text(
                        text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 10.sp,
                        color = Gray400,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun formatDateTime(dateTime: String): String {
    return try {
        val parsedDateTime = LocalDateTime.parse(dateTime)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
        parsedDateTime.format(formatter)
    } catch (e: Exception) {
        try {
            val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val parsedDateTime = LocalDateTime.parse(dateTime, isoFormatter)
            val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
            parsedDateTime.format(displayFormatter)
        } catch (e2: Exception) {
            dateTime
        }
    }
}

fun formatAmount(amount: BigDecimal, transactionType: TransactionType?): String {
    val absAmount = amount.abs()
    val sign = when (transactionType) {
        TransactionType.CREDIT -> "+"
        TransactionType.DEBIT -> "-"
        else -> if (amount >= BigDecimal.ZERO) "+" else "-"
    }
    return "${sign}KD ${String.format("%.3f", absAmount)}"
}

fun determineTransactionColor(transaction: TransactionResponse): Color {
    return when (transaction.transactionType) {
        TransactionType.CREDIT -> Success
        TransactionType.DEBIT -> Error
        else -> Gray500
    }
}

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
        "dining", "food", "restaurant", "fast food" -> CategoryDining
        "shopping", "retail", "grocery" -> CategoryEntertainment
        "transport", "transportation", "fuel", "gas" -> CategoryShopping
        "income", "salary", "deposit" -> Success
        "entertainment", "movies", "gaming" -> CategoryUtilities
        "bills", "utilities", "phone", "internet" -> Gray500
        "healthcare", "medical", "pharmacy" -> Error
        "education", "books", "school" -> Purple
        "travel", "hotel", "airline" -> Cyan
        "insurance" -> CategoryTransport
        "investment", "stocks" -> NBKBlue
        "cash", "atm" -> Success
        "transfer" -> CategoryEntertainment
        else -> Info
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