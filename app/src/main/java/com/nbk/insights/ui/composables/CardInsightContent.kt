package com.nbk.insights.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.data.dtos.BankCardDTO
import com.nbk.insights.ui.theme.InsightsTheme

import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.nbk.insights.ui.theme.*


data class SpendingCategory(
    val name: String,
    val amount: Float,
    val percentage: Float,
    val color: Color,
    val icon: ImageVector
)

data class RecentTransaction(
    val title: String,
    val category: String,
    val amount: String,
    val time: String,
    val icon: ImageVector,
    val iconColor: Color
)

data class BudgetLimit(
    val category: String,
    val spent: BigDecimal,
    val limit: BigDecimal,
    val color: Color,
    val icon: ImageVector,
    val isOverBudget: Boolean = false,
    val isNearLimit: Boolean = false,
    val renewsAt: String // Format: "yyyy-MM-dd"
)

// Extension functions for convenience
fun BudgetLimit.getSpentPercentage(): Double {
    return if (limit > BigDecimal.ZERO) {
        (spent.divide(limit, 4, java.math.RoundingMode.HALF_UP).toDouble() * 100)
    } else {
        0.0
    }
}

fun BudgetLimit.getRemainingAmount(): BigDecimal {
    return (limit - spent).max(BigDecimal.ZERO)
}

fun BudgetLimit.isNearLimit(threshold: Double = 80.0): Boolean {
    return getSpentPercentage() >= threshold
}

fun BudgetLimit.isOverBudget(): Boolean {
    return spent > limit
}

@Composable
fun CardInsightContent(
    card: BankCardDTO,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf("Monthly") }
    var selectedMonth by remember { mutableStateOf("Dec\n2024") }

    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var selectedBudgetForEdit by remember { mutableStateOf<BudgetLimit?>(null) }


    var budgetLimits by remember {
        mutableStateOf(listOf(
            BudgetLimit("Dining", 450f, 400f, CategoryDining, Icons.Default.Restaurant, isOverBudget = true),
            BudgetLimit("Shopping", 680f, 800f, CategoryShopping, Icons.Default.ShoppingBag),
            BudgetLimit("Transport", 230f, 300f, CategoryTransport, Icons.Default.DirectionsCar),
            BudgetLimit("Entertainment", 180f, 200f, CategoryEntertainment, Icons.Default.Movie, isNearLimit = true)

        ))
    }

    val periods = listOf("Daily", "Weekly", "Monthly", "Yearly")
    val months = listOf("Oct\n2024", "Nov\n2024", "Dec\n2024", "Jan\n2025")

    val spendingData = listOf(3500f, 3200f, 3800f)

    val spendingCategories = listOf(
        SpendingCategory("Dining", 1140f, 30f, CategoryDining, Icons.Default.Restaurant),
        SpendingCategory("Shopping", 912f, 24f, CategoryShopping, Icons.Default.ShoppingBag),
        SpendingCategory("Transport", 608f, 16f, CategoryTransport, Icons.Default.DirectionsCar),
        SpendingCategory("Entertainment", 456f, 12f, CategoryEntertainment, Icons.Default.Movie),
        SpendingCategory("Utilities", 380f, 10f, CategoryUtilities, Icons.Default.Bolt),
        SpendingCategory("Healthcare", 304f, 8f, CategoryHealthcare, Icons.Default.LocalHospital)
    )

    val recentTransactions = listOf(
        RecentTransaction("Starbucks Coffee", "Dining", "-KD 4.50", "Today 2:30 PM", Icons.Default.Restaurant, CategoryDining),
        RecentTransaction("Amazon Purchase", "Shopping", "-KD 67.20", "Yesterday 4:15 PM", Icons.Default.ShoppingBag, CategoryShopping),
        RecentTransaction("Uber Ride", "Transport", "-KD 12.30", "Dec 20, 8:45 PM", Icons.Default.DirectionsCar, CategoryTransport)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = NBKBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Insights",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Spending Analytics",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Row {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(periods) { period ->
                        Button(
                            onClick = { selectedPeriod = period },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedPeriod == period) NBKBlue else Color.Transparent,
                                contentColor = if (selectedPeriod == period) Color.White else NBKBlue
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(period, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(months) { month ->
                    Card(
                        onClick = { selectedMonth = month },
                        modifier = Modifier.width(80.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedMonth == month) NBKBlue else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = month,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedMonth == month) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Spending",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SpendingBarChart(spendingData)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "December 2024 Breakdown",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SpendingPieChart(spendingCategories)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(spendingCategories.chunked(2)) { categoryPair ->
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categoryPair.forEach { category ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(category.color, CircleShape)
                                        )
                                        Text(
                                            text = category.name,
                                            fontSize = 12.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Transactions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        TextButton(onClick = { }) {
                            Text("All Categories", color = NBKBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    recentTransactions.forEach { transaction ->
                        RecentTransactionItem(transaction)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Show More Transactions", color = NBKBlue)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Budget Limits",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        IconButton(
                            onClick = { showAddBudgetDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    NBKBlueAlpha10,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Budget Limit",
                                tint = NBKBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (budgetLimits.isNotEmpty()) {
                        budgetLimits.forEachIndexed { index, budget ->
                            BudgetLimitItem(
                                budget = budget,
                                onClick = {
                                    selectedBudgetForEdit = budget
                                    showEditBudgetDialog = true
                                }
                            )
                            if (index < budgetLimits.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Budget Limits Set",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = "Tap the + button to add your first budget limit",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddBudgetDialog) {
        BudgetLimitDialog(
            onDismiss = { showAddBudgetDialog = false },

            onConfirm = { category, limit ->
                val newBudget = BudgetLimit(
                    category = category,
                    spent = 0f,
                    limit = limit,
                    color = when (category) {
                        "Dining" -> CategoryDining
                        "Shopping" -> CategoryShopping
                        "Transport" -> CategoryTransport
                        "Entertainment" -> CategoryEntertainment
                        "Utilities" -> CategoryUtilities
                        "Healthcare" -> CategoryHealthcare
                        else -> CategoryOther

                    },
                    icon = when (category.name) {
                        "DINING" -> Icons.Default.Restaurant
                        "SHOPPING" -> Icons.Default.ShoppingBag
                        "TRANSPORT" -> Icons.Default.DirectionsCar
                        "ENTERTAINMENT" -> Icons.Default.Movie
                        "FOOD_AND_GROCERIES" -> Icons.Default.ShoppingCart
                        "OTHER" -> Icons.Default.MoreHoriz
                        else -> Icons.Default.MoreHoriz
                    },
                    renewsAt = renewsAt
                )
                budgetLimits = budgetLimits + newBudget
                showAddBudgetDialog = false
            }
        )
    }

    if (showEditBudgetDialog && selectedBudgetForEdit != null) {
        EditBudgetDialog(
            budget = selectedBudgetForEdit!!,
            onDismiss = {
                showEditBudgetDialog = false
                selectedBudgetForEdit = null
            },

            onUpdate = { newLimit, renewsAt ->
                // Update the budget limit
                budgetLimits = budgetLimits.map { budget ->
                    if (budget.category == selectedBudgetForEdit!!.category) {
                        budget.copy(
                            limit = newLimit,
                            renewsAt = renewsAt,
                            isOverBudget = budget.spent > newLimit,
                            isNearLimit = !budget.isOverBudget() && budget.getSpentPercentage() >= 80.0
                        )
                    } else {
                        budget
                    }
                }
                showEditBudgetDialog = false
                selectedBudgetForEdit = null
            },
            onDelete = {
                budgetLimits = budgetLimits.filter { it.category != selectedBudgetForEdit!!.category }
                showEditBudgetDialog = false
                selectedBudgetForEdit = null
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CardInsightContentPreview() {
    InsightsTheme {
        CardInsightContent(
            card = BankCardDTO(
                type = "Debit Card",
                name = "NBK Titanium",
                lastFourDigits = "5678",
                balance = "KD 3,456.78",
                expiryDate = "05/26"
            ),
            onDismiss = { }
        )
    }
}