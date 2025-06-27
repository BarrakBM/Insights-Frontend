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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.TransactionResponse
import com.nbk.insights.navigation.Screen
import com.nbk.insights.ui.theme.*
import com.nbk.insights.ui.theme.NBKBlue
import com.nbk.insights.ui.theme.PurpleGrey40
import kotlin.math.min

@Composable
fun RecentTransactions2(
    navController: NavController,
    visibleTx: List<TransactionResponse>,
    totalTx: Int,
    shownCount: Int,
    onShownCountChange: (Int) -> Unit
) {
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
                    "Recent Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                TextButton(onClick = { navController.navigate(Screen.AllTransactions.route) }) {
                    Text("View All", color = NBKBlue)
                }
            }

            // Transaction list
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                visibleTx.forEach { tx ->
                    TransactionItem(transaction = tx)
                }
            }

            // Show More / Show Less
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
    }
}