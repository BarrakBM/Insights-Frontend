package com.nbk.insights.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.ui.composables.BalanceCard
import com.nbk.insights.ui.composables.GreetingSection
import com.nbk.insights.ui.composables.QuickInsights
import com.nbk.insights.ui.composables.RecentTransactions2
import com.nbk.insights.ui.composables.SpendingViewAllChart
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.AccountsViewModel
import com.nbk.insights.viewmodels.AuthViewModel
import com.nbk.insights.viewmodels.TransactionsViewModel
import com.nbk.insights.viewmodels.RecommendationsViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen2(navController: NavController, paddingValues: PaddingValues) {
    /* ── view-models with ACTIVITY SCOPE ─────────────────────────────── */
    val activity = LocalActivity.current as ComponentActivity
    val authVM: AuthViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = remember { AppInitializer.provideAuthViewModelFactory(activity) }
    )
    val accountsVM: AccountsViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = remember { AppInitializer.provideAccountsViewModelFactory(activity) }
    )
    val txVM: TransactionsViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = remember { AppInitializer.provideTransactionsViewModelFactory(activity) }
    )
    val recommendationsVM: RecommendationsViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = remember { AppInitializer.provideRecommendationsViewModelFactory(activity) }
    )

    /* ── state ───────────────────────────────────── */
    val recentTxs by txVM.userTransactions
    val totalTx = recentTxs?.size ?: 0

    // Get last month and this month data
    val lastMonthData by txVM.lastMonth
    val thisMonthData by txVM.thisMonth

    var shownCount by remember { mutableIntStateOf(4) }
    val visibleTx = recentTxs.orEmpty().take(shownCount)

    val firstName = authVM.user.value?.fullName?.split(" ")?.firstOrNull() ?: "Guest"
    val totalBalance = accountsVM.totalBalance.value?.totalBalance ?: BigDecimal.ZERO

    var isBalanceVisible by remember { mutableStateOf(false) }

    val pullState = rememberPullToRefreshState()

    val isRefreshing = txVM.isRefreshing.value ||
            accountsVM.isRefreshing.value ||
            recommendationsVM.isRefreshing.value

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
        if (txVM.lastMonth.value == null) {
            txVM.fetchLastMonth()
        }
        if (txVM.thisMonth.value == null) {
            txVM.fetchThisMonth()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                txVM.fetchUserTransactions(forceRefresh = true)

                launch { accountsVM.fetchUserAccounts() }
                launch { accountsVM.fetchTotalBalance() }
                launch { txVM.fetchLastMonth() }
                launch { txVM.fetchThisMonth() }
                launch { recommendationsVM.fetchQuickInsights() }
            }
        },
        state = pullState,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { GreetingSection(firstName) }
            item {
                BalanceCard(
                    balance = "KD $totalBalance",
                    isBalanceVisible = isBalanceVisible,
                    onToggleVisibility = { isBalanceVisible = !isBalanceVisible }
                )
            }
            item {
                SpendingViewAllChart(
                    lastMonthData = lastMonthData,
                    thisMonthData = thisMonthData
                )
            }
            item { QuickInsights() }
            item {
                RecentTransactions2(
                    navController = navController,
                    visibleTx = visibleTx,
                    totalTx = totalTx,
                    shownCount = shownCount,
                    onShownCountChange = { shownCount = it }
                )
            }
        }
    }
}

// Extension function for colored shadow
fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.12f,
    borderRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    blurRadius: Dp = 0.dp
) = this.then(Modifier)