package com.nbk.insights.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.CashFlowCategorizedResponse
import com.nbk.insights.ui.composables.BalanceCard
import com.nbk.insights.ui.composables.GreetingSection
import com.nbk.insights.ui.composables.QuickInsights
import com.nbk.insights.ui.composables.RecentTransactions2
import com.nbk.insights.ui.composables.SpendingViewAllChart
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.AccountsViewModel
import com.nbk.insights.viewmodels.AuthViewModel
import com.nbk.insights.viewmodels.TransactionsViewModel
import java.math.BigDecimal

@Composable
fun HomeScreen2(navController: NavController, paddingValues: PaddingValues) {
    /* ── view-models ─────────────────────────────── */
    val ctx = LocalContext.current
    val authVM: AuthViewModel = viewModel(factory = remember { AppInitializer.provideAuthViewModelFactory(ctx) })
    val accountsVM: AccountsViewModel = viewModel(factory = remember { AppInitializer.provideAccountsViewModelFactory(ctx) })
    val txVM: TransactionsViewModel = viewModel(factory = remember { AppInitializer.provideTransactionsViewModelFactory(ctx) })

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

    LaunchedEffect(Unit) {
        accountsVM.fetchUserAccounts()
        accountsVM.fetchTotalBalance()
        txVM.fetchUserTransactions()
        txVM.fetchLastMonth()
        txVM.fetchThisMonth()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
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

// Extension function for colored shadow
fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.12f,
    borderRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    blurRadius: Dp = 0.dp
) = this.then(Modifier)