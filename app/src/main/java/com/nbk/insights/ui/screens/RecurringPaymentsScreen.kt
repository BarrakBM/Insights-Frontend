package com.nbk.insights.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    // se activity-scoped ViewModels
    val activity = LocalActivity.current as ComponentActivity
    val transactionsVM: TransactionsViewModel = viewModel(
        viewModelStoreOwner = activity, // 👈 SHARED ACROSS ALL SCREENS
        factory = remember { AppInitializer.provideTransactionsViewModelFactory(activity) }
    )
    val accountsVM: AccountsViewModel = viewModel(
        viewModelStoreOwner = activity, // 👈 SHARED ACROSS ALL SCREENS
        factory = remember { AppInitializer.provideAccountsViewModelFactory(activity) }
    )

    // Smart data fetching - only fetch if data doesn't exist
    LaunchedEffect(Unit) {
        if (accountsVM.accounts.value == null) {
            accountsVM.fetchUserAccounts()
        }
    }

    val accounts = accountsVM.accounts.value?.accounts
    val firstAccountId = accounts?.firstOrNull()?.accountId

    // Only fetch recurring payments if we don't have them
    LaunchedEffect(firstAccountId) {
        firstAccountId?.let {
            if (transactionsVM.recurringPayments.value == null) {
                transactionsVM.detectRecurringPayments(it)
            }
        }
    }

    // Get real recurring payments data
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
            // Use real data
            recurringPayments?.let { payments ->
                if (payments.isEmpty()) {
                    // Show empty state
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "No recurring payments detected",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    "We'll analyze your transactions to find recurring patterns",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    // Show real recurring payments
                    items(payments) { payment ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    payment.mcc.category,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                Text(
                                    "Amount: KD ${payment.latestAmount}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    "Confidence: ${String.format("%.1f", payment.confidenceScore * 100)}%",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    "Frequency: ${payment.monthsWithPayments} months",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            } ?: run {
                // Show loading state
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = NBKBlue)
                            Text(
                                "Analyzing transactions...",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}