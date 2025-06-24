package com.nbk.insights.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.ui.composables.BottomNavigationBar
import com.nbk.insights.ui.theme.*
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.TransactionsViewModel
import com.nbk.insights.viewmodels.AccountsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringPaymentsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val transactionsVM: TransactionsViewModel = viewModel(
        factory = remember { AppInitializer.provideTransactionsViewModelFactory(ctx) }
    )
    val accountsVM: AccountsViewModel = viewModel(
        factory = remember { AppInitializer.provideAccountsViewModelFactory(ctx) }
    )

    LaunchedEffect(Unit) {
        accountsVM.fetchUserAccounts()
    }

    val accounts = accountsVM.accounts.value?.accounts
    val firstAccountId = accounts?.firstOrNull()?.accountId

    LaunchedEffect(firstAccountId) {
        firstAccountId?.let {
            transactionsVM.detectRecurringPayments(it)
        }
    }

    val recurringPayments = transactionsVM.recurringPayments.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Recurring Payments", color = Color.White, fontSize = 20.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NBKBlue)
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedTab = "Recurring", navController = navController)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            recurringPayments?.let { payments ->
                items(payments) { payment ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(payment.mcc.category, fontSize = 16.sp, color = Color.Black)
                            Text("Amount: KD ${payment.latestAmount}", fontSize = 14.sp, color = Color.Gray)
                            Text("Confidence: ${String.format("%.1f", payment.confidenceScore * 100)}%", fontSize = 14.sp, color = Color.Gray)
                            Text("Frequency: ${payment.monthsWithPayments} months", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            } ?: item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}