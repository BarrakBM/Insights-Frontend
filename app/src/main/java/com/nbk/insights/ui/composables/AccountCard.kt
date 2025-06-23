package com.nbk.insights.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nbk.insights.data.dtos.Account
import com.nbk.insights.data.dtos.AccountType
import java.math.BigDecimal
import java.text.DecimalFormat

@Composable
fun AccountCard(
    account: Account,
    onViewInsights: () -> Unit,
    onViewTransactions: (Long) -> Unit, // Changed to accept accountId
    onSetBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Account icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFF00897B).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (account.accountType == AccountType.SAVINGS)
                                Icons.Default.Savings else Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = Color(0xFF00897B),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (account.accountType == AccountType.MAIN)
                                "Checking Account" else "Savings Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212121)
                        )
                        Text(
                            text = "•••• ${account.cardNumber.takeLast(4)}",
                            fontSize = 13.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                // Balance
                Text(
                    text = "$${formatBalance(account.balance)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionPill(
                    text = "Insights",
                    icon = Icons.Default.BarChart,
                    onClick = onViewInsights,
                    modifier = Modifier.weight(1f)
                )

                ActionPill(
                    text = "Transactions",
                    icon = Icons.Default.List,
                    onClick = { onViewTransactions(account.accountId) }, // Pass accountId
                    modifier = Modifier.weight(1f)
                )

                ActionPill(
                    text = "Budget",
                    icon = Icons.Default.AccountBalanceWallet,
                    onClick = onSetBudget,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActionPill(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(36.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF1A237E)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

private fun formatBalance(balance: BigDecimal): String {
    val formatter = DecimalFormat("#,###.##")
    return formatter.format(balance)
}

@Preview(showBackground = true, backgroundColor = 0xFFFAFAFA)
@Composable
fun AccountCardPreview() {
    val sampleAccount = Account(
        accountId = 1L,
        accountType = AccountType.MAIN,
        accountNumber = "123456784521",
        balance = BigDecimal("12847.32"),
        cardNumber = "Debit Card"
    )

    AccountCard(
        account = sampleAccount,
        onViewInsights = {},
        onViewTransactions = { accountId -> /* Handle navigation */ },
        onSetBudget = {}
    )
}