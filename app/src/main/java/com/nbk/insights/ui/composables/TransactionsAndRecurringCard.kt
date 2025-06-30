package com.nbk.insights.ui.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.RecurringPaymentResponse
import com.nbk.insights.data.dtos.TransactionResponse
import com.nbk.insights.navigation.Screen
import com.nbk.insights.ui.theme.*
import kotlin.math.min

@Composable
fun TransactionsAndRecurringCard(
    navController: NavController,
    // Recent Transactions data
    visibleTx: List<TransactionResponse>,
    totalTx: Int,
    shownCount: Int,
    onShownCountChange: (Int) -> Unit,
    // Recurring Payments data
    recurringPayments: List<RecurringPaymentResponse>?,
    isLoadingRecurring: Boolean = false
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Recent, 1 = Recurring
    var recurringShownCount by remember { mutableStateOf(3) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Tab Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedTab == 0) "Recent Transactions" else "Recurring Payments",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    TextButton(
                        onClick = {
                            navController.navigate(
                                if (selectedTab == 0) Screen.AllTransactions.route
                                else Screen.RecurringPayments.route
                            )
                        }
                    ) {
                        Text("View All", color = NBKBlue)
                    }
                }

                // Tab Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabButton(
                        text = "Recent",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Recurring",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                0 -> {
                    // Recent Transactions Content
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        visibleTx.forEach { tx ->
                            TransactionItem(transaction = tx)
                        }
                    }

                    // Show More / Show Less for Transactions
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

                1 -> {
                    // Recurring Payments Content
                    when {
                        isLoadingRecurring -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = NBKBlue,
                                    modifier = Modifier.padding(8.dp)
                                )
                                Text(
                                    "Analyzing transactions...",
                                    fontSize = 14.sp,
                                    color = Gray500
                                )
                            }
                        }

                        recurringPayments.isNullOrEmpty() -> {
                            Text(
                                "No recurring payments detected",
                                fontSize = 14.sp,
                                color = Gray500,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )
                        }

                        else -> {
                            val visiblePayments = recurringPayments.take(recurringShownCount)

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                visiblePayments.forEach { payment ->
                                    RecurringPaymentCard(
                                        payment = payment,
                                        onClick = { /* Optional navigation */ }
                                    )
                                }
                            }

                            // Show More / Show Less for Recurring
                            if (recurringPayments.size > 3) {
                                val showMore = recurringShownCount < recurringPayments.size
                                val showLess = recurringShownCount > 3

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (showLess) {
                                        Text(
                                            text = "Show Less",
                                            fontWeight = FontWeight.Medium,
                                            color = PurpleGrey40,
                                            modifier = Modifier.clickable { recurringShownCount = 3 }
                                        )
                                    }

                                    if (showLess && showMore) Spacer(Modifier.width(24.dp))

                                    if (showMore) {
                                        Text(
                                            text = "Show More",
                                            fontWeight = FontWeight.Medium,
                                            color = NBKBlue,
                                            modifier = Modifier.clickable {
                                                recurringShownCount = min(recurringShownCount + 3, recurringPayments.size)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isSelected) NBKBlue else Gray100
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Gray600
        )
    }
}