package com.nbk.insights.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nbk.insights.ui.composables.*
import com.nbk.insights.data.tempfunctions.*
import com.nbk.insights.viewmodels.*
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onViewAllCards: () -> Unit,
    onViewAllTransactions: () -> Unit,
    navController: NavController,
    authViewModel: AuthViewModel,
    accountsViewModel: AccountsViewModel
) {
    val recentTransactions = remember { getRecentTransactions() }
    val bankCards = remember { getBankCards() }
    val firstName = authViewModel.user.value?.fullName?.split(" ")?.firstOrNull() ?: "Guest"
    val totalBalance = accountsViewModel.totalBalance.value?.totalBalance ?: BigDecimal.ZERO

    LaunchedEffect(Unit) {
        accountsViewModel.fetchTotalBalance()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column() {
                        Text(
                            text = "Hello, $firstName",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Welcome back!",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E3A8A))
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedTab = "Home", navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TotalBalanceCard(
                    balance = "KD ${totalBalance}",
                    lastUpdated = "Today, 10:45 AM"
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("My Cards", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onViewAllCards) {
                        Text("View All Cards", color = Color(0xFF1E3A8A))
                    }
                }
            }
            items(bankCards) { CardItem(card = it) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onViewAllTransactions) {
                        Text("View All", color = Color(0xFF1E3A8A))
                    }
                }
            }
            items(recentTransactions.take(5)) { TransactionItem(transaction = it) }
        }
    }
}