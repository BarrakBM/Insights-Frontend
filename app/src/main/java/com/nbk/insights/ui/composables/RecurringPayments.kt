package com.nbk.insights.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.RecurringPaymentResponse
import com.nbk.insights.navigation.Screen
import com.nbk.insights.ui.theme.*
import kotlin.math.min

@Composable
fun RecurringPayments(
    navController: NavController,
    recurringPayments: List<RecurringPaymentResponse>?,
    isLoading: Boolean = false
) {
    var shownCount by remember { mutableStateOf(3) }

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recurring Payments",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                TextButton(onClick = { navController.navigate(Screen.RecurringPayments.route) }) {
                    Text("View All", color = NBKBlue)
                }
            }

            when {
                // Loading state
                isLoading -> {
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

                // Empty state
                recurringPayments.isNullOrEmpty() -> {
                    Text(
                        "No recurring payments detected",
                        fontSize = 14.sp,
                        color = Gray500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                }

                // Show payments
                else -> {
                    val visiblePayments = recurringPayments.take(shownCount)

                    // Recurring payments list
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        visiblePayments.forEach { payment ->
                            RecurringPaymentCard(
                                payment = payment,
                                onClick = {
                                    // Optional: Navigate to payment details
                                }
                            )
                        }
                    }

                    // Show More / Show Less
                    if (recurringPayments.size > 3) {
                        val showMore = shownCount < recurringPayments.size
                        val showLess = shownCount > 3

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (showLess) {
                                Text(
                                    text = "Show Less",
                                    fontWeight = FontWeight.Medium,
                                    color = PurpleGrey40,
                                    modifier = Modifier.clickable { shownCount = 3 }
                                )
                            }

                            if (showLess && showMore) Spacer(Modifier.width(24.dp))

                            if (showMore) {
                                Text(
                                    text = "Show More",
                                    fontWeight = FontWeight.Medium,
                                    color = NBKBlue,
                                    modifier = Modifier.clickable {
                                        shownCount = min(shownCount + 3, recurringPayments.size)
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