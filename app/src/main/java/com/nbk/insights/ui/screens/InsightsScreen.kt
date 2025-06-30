package com.nbk.insights.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.*
import com.nbk.insights.ui.theme.*
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.AccountsViewModel
import java.math.RoundingMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.nbk.insights.ui.composables.SmartRecommendationsDrawer
import com.nbk.insights.ui.composables.TransactionsAndRecurringCard
import com.nbk.insights.viewmodels.RecommendationsViewModel
import com.nbk.insights.viewmodels.TransactionsViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(navController: NavController, paddingValues: PaddingValues) {
    val activity = LocalActivity.current as ComponentActivity
    val accountsViewModel: AccountsViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = remember { AppInitializer.provideAccountsViewModelFactory(activity) }
    )

    val transactionViewModel: TransactionsViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = remember { AppInitializer.provideTransactionsViewModelFactory(activity) }
    )

    val recommendationsViewModel: RecommendationsViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = remember { AppInitializer.provideRecommendationsViewModelFactory(activity) }
    )

    val pullState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    val recentTxs by transactionViewModel.accountTransactions
    val totalTx = recentTxs?.size ?: 0
    var shownCount by remember { mutableIntStateOf(4) }
    val visibleTx = recentTxs.orEmpty().take(shownCount)

    val recurringPayments by transactionViewModel.recurringPayments

    val account by accountsViewModel.selectedAccount
    val accountsResponse by accountsViewModel.accounts
    val accountsList = accountsResponse?.accounts ?: emptyList()

    val budgetAdherence by accountsViewModel.budgetAdherence
    val accountLoading by accountsViewModel.isLoading

    val thisMonthCashFlow by transactionViewModel.thisMonthAccount
    val transactionLoading by transactionViewModel.isLoading
    val accountInsightsCache by transactionViewModel.accountInsightsCache

    val recommendations by recommendationsViewModel.categoryRecommendations

    val selectedAccountId = account?.accountId

    val pagerState = rememberPagerState(pageCount = { accountsList.size })

    LaunchedEffect(Unit) {
        if (accountsViewModel.accounts.value == null) {
            accountsViewModel.fetchUserAccounts()
        }
        if (accountsViewModel.budgetAdherence.value == null) {
            accountsViewModel.fetchBudgetAdherence()
        }
        if (accountsViewModel.spendingTrends.value == null) {
            accountsViewModel.fetchSpendingTrends()
        }
    }

    LaunchedEffect(selectedAccountId) {
        selectedAccountId?.let { id ->
            shownCount = 4

            val cached = accountInsightsCache[id]

            if (cached != null) {
                transactionViewModel.setAccountInsightsFromCache(id)
            } else {
                transactionViewModel.clearAccountTransactions()
                transactionViewModel.fetchAccountTransactions(id)
                transactionViewModel.detectRecurringPayments(id)
                transactionViewModel.fetchThisMonthAccount(id)
            }
        }
    }




    LaunchedEffect(pagerState.currentPage) {
        if (accountsList.isNotEmpty() && pagerState.currentPage < accountsList.size) {
            accountsViewModel.setSelectedAccount(accountsList[pagerState.currentPage])
        }
    }

    PullToRefreshBox(
        isRefreshing = transactionViewModel.isLoading.value || accountsViewModel.isLoading.value,
        onRefresh = {
            coroutineScope.launch {
                shownCount = 4
                transactionViewModel.clearAccountTransactions()
                transactionViewModel.clearCache()
                transactionViewModel.fetchAccountTransactions(account?.accountId ?: return@launch)
                transactionViewModel.detectRecurringPayments(account!!.accountId)
                transactionViewModel.fetchThisMonthAccount(account!!.accountId)

                accountsViewModel.fetchUserAccounts()
                accountsViewModel.fetchBudgetAdherence()
                accountsViewModel.fetchSpendingTrends()
            }
        },
        state = pullState,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if (accountsList.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { index ->
                        AccountCard(account = accountsList[index]!!)
                    }
                } else {
                    AccountCard(account = Account(
                        accountId = 0L,
                        accountType = AccountType.MAIN,
                        accountNumber = "xxxxxxxxx",
                        balance = BigDecimal.ZERO,
                        cardNumber = "xxxxxxxxx"
                    ))
                }
            }

            item {
                MoneyFlowSection(
                    cashFlow = thisMonthCashFlow,
                    isLoading = transactionLoading
                )
            }
            item {
                TransactionsAndRecurringCard(
                    navController = navController,
                    visibleTx = visibleTx,
                    totalTx = totalTx,
                    shownCount = shownCount,
                    onShownCountChange = { shownCount = it },
                    recurringPayments = recurringPayments,
                    isLoadingRecurring = transactionViewModel.isLoading.value
                )
            }

            item {
                BudgetProgressWithData(
                    budgetAdherence = budgetAdherence,
                    isLoading = accountLoading,
                    onNavigateToBudget = {
                        navController.navigate("budget_management")
                    }
                )
            }

            if (!recommendations.isNullOrEmpty()) {
                item {
                    SmartRecommendationsDrawer(recommendations = recommendations!!)
                }
            }
        }
    }

}

@Composable
fun MoneyFlowSection(
    cashFlow: CashFlowCategorizedResponse?,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isLoading) {
            // Loading state
            repeat(3) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .shadow(2.dp, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        } else {
            val moneyIn = cashFlow?.moneyIn ?: BigDecimal.ZERO
            val moneyOut = cashFlow?.moneyOut ?: BigDecimal.ZERO
            val net = cashFlow?.netCashFlow ?: BigDecimal.ZERO


            MoneyFlowCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ArrowDownward,
                label = "Money In",
                amount = "KD ${moneyIn.setScale(3, RoundingMode.HALF_UP)}",
                color = SuccessGreen
            )
            MoneyFlowCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ArrowUpward,
                label = "Money Out",
                amount = "KD ${moneyOut.setScale(3, RoundingMode.HALF_UP)}",
                color = Color.Red
            )
            MoneyFlowCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Savings,
                label = "net",
                amount = "KD ${net.abs().setScale(3, RoundingMode.HALF_UP)}",
                color = if (net >= BigDecimal.ZERO) PrimaryBlue else Color.Red,
            )
        }
    }
}

@Composable
fun MoneyFlowCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    amount: String,
    color: Color,
) {
    Card(
        modifier = modifier.shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                label,
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun BudgetProgressWithData(
    budgetAdherence: BudgetAdherenceResponse?,
    isLoading: Boolean,
    onNavigateToBudget: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Budget Progress",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            TextButton(onClick = onNavigateToBudget) {
                Text(
                    "Manage",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
            }
        }

        when {
            isLoading -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            budgetAdherence?.categoryAdherences?.isEmpty() != false -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = onNavigateToBudget
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = PrimaryBlue.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "No budgets set yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            "Tap here to start budgeting",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            else -> {
                val topCategories = budgetAdherence.categoryAdherences
                    .sortedByDescending { it.percentageUsed }
                    .take(3)

                topCategories.forEach { categoryAdherence ->
                    BudgetItemFromData(categoryAdherence)
                }

                if (budgetAdherence.categoryAdherences.size > 3) {
                    TextButton(
                        onClick = onNavigateToBudget,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "View all ${budgetAdherence.categoryAdherences.size} budgets →",
                            fontSize = 14.sp,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetItemFromData(categoryAdherence: CategoryAdherence) {
    val icon = getBudgetCategoryIcon(categoryAdherence.category)
    val color = getBudgetCategoryColor(categoryAdherence.adherenceLevel)
    val status = getBudgetStatusText(categoryAdherence)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        categoryAdherence.category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
                Text(
                    "KD ${categoryAdherence.spentAmount.setScale(3, RoundingMode.HALF_UP)} / " +
                            "KD ${
                                categoryAdherence.budgetAmount.setScale(
                                    3,
                                    RoundingMode.HALF_UP
                                )
                            }",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            LinearProgressIndicator(
                progress = {
                    (categoryAdherence.percentageUsed / 100f).coerceIn(0.0, 1.0).toFloat()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    status,
                    fontSize = 12.sp,
                    color = color
                )

                if (categoryAdherence.spendingTrend != SpendingTrend.NO_DATA) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (categoryAdherence.spendingTrend) {
                                SpendingTrend.INCREASED -> Icons.Default.TrendingUp
                                SpendingTrend.DECREASED -> Icons.Default.TrendingDown
                                else -> Icons.Default.TrendingFlat
                            },
                            contentDescription = null,
                            tint = when (categoryAdherence.spendingTrend) {
                                SpendingTrend.INCREASED -> WarningAmber
                                SpendingTrend.DECREASED -> SuccessGreen
                                else -> TextSecondary
                            },
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            "${categoryAdherence.spendingChangePercentage.toInt()}% vs last month",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// Helper functions
private fun getBudgetCategoryIcon(category: String): ImageVector {
    return when (category.uppercase()) {
        "DINING" -> Icons.Default.Restaurant
        "GROCERIES", "FOOD_AND_GROCERIES" -> Icons.Default.ShoppingCart
        "ENTERTAINMENT" -> Icons.Default.SportsEsports
        "SHOPPING" -> Icons.Default.ShoppingBag
        "TRANSPORT", "TRANSPORTATION" -> Icons.Default.DirectionsCar
        "UTILITIES" -> Icons.Default.Bolt
        "HEALTHCARE" -> Icons.Default.LocalHospital
        else -> Icons.Default.Category
    }
}

private fun getBudgetCategoryColor(adherenceLevel: AdherenceLevel): Color {
    return when (adherenceLevel) {
        AdherenceLevel.EXCELLENT -> PrimaryBlue
        AdherenceLevel.GOOD -> SuccessGreen
        AdherenceLevel.WARNING -> WarningAmber
        AdherenceLevel.CRITICAL -> Color(0xFFFF6B6B)
        AdherenceLevel.EXCEEDED -> Color.Red
    }
}

private fun getBudgetStatusText(categoryAdherence: CategoryAdherence): String {
    val percentageUsed = categoryAdherence.percentageUsed.toInt()
    return when (categoryAdherence.adherenceLevel) {
        AdherenceLevel.EXCELLENT -> "$percentageUsed% used - Doing great"
        AdherenceLevel.GOOD -> "$percentageUsed% used - On track"
        AdherenceLevel.WARNING -> "$percentageUsed% used - Watch your spending"
        AdherenceLevel.CRITICAL -> "$percentageUsed% used - Near limit!"
        AdherenceLevel.EXCEEDED -> "Over budget by ${(percentageUsed - 100)}%"
    }
}

@Composable
fun AccountCard(account: Account) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryBlue, Color(0xFF1976D2))
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                account.accountType.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                account.accountNumber,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Current Balance",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        "KD ${account.balance}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Card Number",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        "****${account.cardNumber.takeLast(4)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

//@Composable
//fun FinancialInsights() {
//    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
//        Text(
//            "Financial Insights",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.SemiBold,
//            color = TextPrimary
//        )
//
//        InsightDetailCard(
//            icon = Icons.Default.TrendingUp,
//            title = "Excellent Savings Rate",
//            description = "You're saving 39% of your income this month - well above the recommended 20%!",
//            progress = 0.39f,
//            progressText = "39%",
//            color = SuccessGreen
//        )
//
//        InsightDetailCard(
//            icon = Icons.Default.PieChart,
//            title = "Spending Pattern Analysis",
//            description = "Your largest expense category is housing (45%), followed by food (22%).",
//            actionText = "View detailed breakdown →",
//            color = PrimaryBlue
//        )
//    }
//}

//@Composable
//fun InsightDetailCard(
//    icon: ImageVector,
//    title: String,
//    description: String,
//    progress: Float? = null,
//    progressText: String? = null,
//    actionText: String? = null,
//    color: Color
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .shadow(2.dp, RoundedCornerShape(8.dp)),
//        shape = RoundedCornerShape(8.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .width(4.dp)
//                    .height(80.dp)
//                    .background(color, RoundedCornerShape(2.dp))
//            )
//            Icon(
//                icon,
//                contentDescription = null,
//                tint = color,
//                modifier = Modifier.size(20.dp)
//            )
//            Column(
//                modifier = Modifier.weight(1f),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Text(
//                    title,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = TextPrimary
//                )
//                Text(
//                    description,
//                    fontSize = 12.sp,
//                    color = TextSecondary,
//                    lineHeight = 16.sp
//                )
//                progress?.let {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        LinearProgressIndicator(
//                            progress = { it },
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(8.dp)
//                                .clip(RoundedCornerShape(4.dp)),
//                            color = color,
//                            trackColor = Color.Gray.copy(alpha = 0.2f)
//                        )
//                        progressText?.let { text ->
//                            Text(
//                                text,
//                                fontSize = 12.sp,
//                                fontWeight = FontWeight.Medium,
//                                color = color
//                            )
//                        }
//                    }
//                }
//                actionText?.let {
//                    Text(
//                        it,
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Medium,
//                        color = color
//                    )
//                }
//            }
//        }
//    }
//}
