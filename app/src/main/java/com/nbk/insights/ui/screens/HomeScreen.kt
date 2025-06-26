package com.nbk.insights.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.data.tempfunctions.getBankCards
import com.nbk.insights.navigation.Screen
import com.nbk.insights.ui.composables.*
import com.nbk.insights.ui.theme.*
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.math.min

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // ✅ Use activity scope for ALL ViewModels
    val activity = LocalContext.current as ComponentActivity
    val authVM: AuthViewModel = viewModel(
        viewModelStoreOwner = activity, // 👈 KEY CHANGE
        factory = remember { AppInitializer.provideAuthViewModelFactory(activity) }
    )
    val accountsVM: AccountsViewModel = viewModel(
        viewModelStoreOwner = activity, // 👈 KEY CHANGE
        factory = remember { AppInitializer.provideAccountsViewModelFactory(activity) }
    )
    val txVM: TransactionsViewModel = viewModel(
        viewModelStoreOwner = activity, // 👈 KEY CHANGE
        factory = remember { AppInitializer.provideTransactionsViewModelFactory(activity) }
    )

    /* ── state variables that were missing ───────────────────────────────── */
    val bankCards = remember { getBankCards() }
    val recentTxs by txVM.userTransactions
    val totalTx = recentTxs?.size ?: 0
    var shownCount by remember { mutableStateOf(4) } // how many tx currently shown

    val firstName = authVM.user.value?.fullName?.split(" ")?.firstOrNull() ?: "Guest"
    val totalBalance = accountsVM.totalBalance.value?.totalBalance ?: BigDecimal.ZERO

    /* ── refresh state ───────────────────────────────────── */
    val pullState = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()

    // ✅ Only fetch if data doesn't exist
    LaunchedEffect(Unit) {
        if (accountsVM.accounts.value == null) {
            accountsVM.fetchUserAccounts()
        }
        if (accountsVM.totalBalance.value == null) {
            accountsVM.fetchTotalBalance()
        }
        if (txVM.userTransactions.value == null) {
            txVM.fetchUserTransactions()
        }
    }

    /* ── ui ──────────────────────────────────────── */
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hello, $firstName", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Welcome back!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NBKBlue)
            )
        },
        bottomBar = { BottomNavigationBar(selectedTab = "Home", navController = navController) }
    ) { paddingValues ->
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val visibleTx = recentTxs.orEmpty().take(shownCount)

        PullToRefreshBox(
            state = pullState,
            isRefreshing = txVM.isRefreshing.value,
            onRefresh = {
                txVM.fetchUserTransactions(forceRefresh = true)
            },
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues), // Apply padding from Scaffold
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundLight)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    /* 1 – total balance */
                    item { TotalBalanceCard(balance = "KD $totalBalance", lastUpdated = "Today, 10:45 AM") }

                    /* 2 – spending chart */
                    item { SpendingViewAllChart() }

                    /* 3 – cards header */
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("My Cards", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { /* TODO */ }) { Text("View All Cards", color = NBKBlue) }
                        }
                    }

                    /* 4 – cards carousel (first 2) */
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(start = 4.dp, end = 4.dp)
                        ) {
                            items(bankCards.take(2)) { card ->
                                Box(Modifier.width(screenWidth * 0.85f)) { CardItem(card) }
                            }
                        }
                    }

                    /* 5 – recent tx header */
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { navController.navigate(Screen.AllTransactions.route) }) {
                                Text("View All", color = NBKBlue)
                            }
                        }
                    }

                    /* 6 – visible transactions */
                    items(visibleTx) { tx -> TransactionItem(transaction = tx) }

                    /* 7 – load-more / show-less controls */
                    if (totalTx > 4) {
                        item {
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
                                        modifier = Modifier.clickable { shownCount = 4 }
                                    )
                                }

                                if (showLess && showMore) Spacer(Modifier.width(24.dp)) // wider gap

                                if (showMore) {
                                    Text(
                                        text = "Show More",
                                        fontWeight = FontWeight.Medium,
                                        color = NBKBlue,
                                        modifier = Modifier.clickable {
                                            shownCount = min(shownCount + 4, totalTx)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}