package com.nbk.insights.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.*
import com.nbk.insights.ui.composables.TransactionsAndRecurringCard
import com.nbk.insights.ui.theme.*
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.AccountsViewModel
import com.nbk.insights.viewmodels.RecommendationsViewModel
import com.nbk.insights.viewmodels.TransactionsViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

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
    val recommendationsLoading by recommendationsViewModel.isLoading

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
        if (recommendationsViewModel.categoryRecommendations.value == null) {
            recommendationsViewModel.fetchCategoryRecommendations()
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

                recommendationsViewModel.fetchCategoryRecommendations()
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
                    Column {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth(),
                            pageSpacing = 16.dp, // Space between cards
                            contentPadding = PaddingValues(horizontal = 24.dp) // Shows partial cards on sides
                        ) { index ->
                            AccountCard(
                                account = accountsList[index]!!,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        // Optional: Add a subtle scale effect for non-focused cards
                                        val pageOffset = (
                                                (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
                                                )
                                        val scale = 1f - (kotlin.math.abs(pageOffset) * 0.05f).coerceIn(0f, 0.05f)
                                        scaleX = scale
                                        scaleY = scale

                                        // Optional: Add alpha effect for non-focused cards
                                        alpha = 1f - (kotlin.math.abs(pageOffset) * 0.3f).coerceIn(0f, 0.3f)
                                    }
                            )
                        }

                        // Optional: Page indicators
                        if (accountsList.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(accountsList.size) { index ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (pagerState.currentPage == index) PrimaryBlue
                                                else PrimaryBlue.copy(alpha = 0.3f)
                                            )
                                    )
                                }
                            }
                        }
                    }
                } else {
                    AccountCard(
                        account = Account(
                            accountId = 0L,
                            accountType = AccountType.MAIN,
                            accountNumber = "xxxxxxxxx",
                            balance = BigDecimal.ZERO,
                            cardNumber = "xxxxxxxxx"
                        )
                    )
                }
            }

            item {
                MoneyFlowSection(
                    cashFlow = thisMonthCashFlow,
                    isLoading = transactionLoading
                )
            }
            item {
                BudgetProgressWithData(
                    budgetAdherence = budgetAdherence,
                    recommendations = recommendations,
                    isLoading = accountLoading,
                    isLoadingRecommendations = recommendationsLoading,
                    onNavigateToBudget = {
                        navController.navigate("budget_management")
                    }
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
                label = "Net",
                amount = if (net >= BigDecimal.ZERO) "KD +${
                    net.abs().setScale(3, RoundingMode.HALF_UP)
                }" else "KD -${net.abs().setScale(3, RoundingMode.HALF_UP)}",
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
                color = color,
                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BudgetProgressWithData(
    budgetAdherence: BudgetAdherenceResponse?,
    recommendations: List<CategoryRecommendationResponse>?,
    isLoading: Boolean,
    isLoadingRecommendations: Boolean,
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
                    val recommendation = recommendations?.find {
                        it.category.equals(categoryAdherence.category, ignoreCase = true)
                    }

                    BudgetItemFromData(
                        categoryAdherence = categoryAdherence,
                        recommendation = recommendation,
                        isLoadingRecommendation = isLoadingRecommendations
                    )
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
fun BudgetItemFromData(
    categoryAdherence: CategoryAdherence,
    recommendation: CategoryRecommendationResponse? = null,
    isLoadingRecommendation: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(300),
        label = "rotation"
    )

    val icon = getBudgetCategoryIcon(categoryAdherence.category)
    val color = getBudgetCategoryColor(categoryAdherence.adherenceLevel)
    val status = getBudgetStatusText(categoryAdherence)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        // Main Budget Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    2.dp,
                    RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomStart = if (expanded) 0.dp else 8.dp,
                        bottomEnd = if (expanded) 0.dp else 8.dp
                    )
                ),
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomStart = if (expanded) 0.dp else 8.dp,
                bottomEnd = if (expanded) 0.dp else 8.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

        // Drawer Handle (Always Visible)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 8.dp,
                bottomEnd = 8.dp
            ),
            colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = "Smart Recommendations",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Smart Recommendations",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(rotationAngle),
                    tint = PrimaryBlue
                )
            }
        }
        // Expandable Recommendation Content
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(
                animationSpec = tween(150)
            ),
            exit = shrinkVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeOut(
                animationSpec = tween(150)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp) // Small gap between main card and recommendation
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(12.dp),
                            clip = false
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFB) // Slight gray tint for visual separation
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color(0xFFE8ECEF) // Subtle border
                    )
                ) {
                    when {
                        isLoadingRecommendation -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = PrimaryBlue,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        "Loading recommendation...",
                                        fontSize = 13.sp,
                                        color = TextSecondary.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        recommendation != null -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Recommendation Icon
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Recommendation Text
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        "Recommendation",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                    )
                                    Text(
                                        recommendation.recommendation,
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = TextSecondary.copy(alpha = 0.5f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        "No recommendations available",
                                        fontSize = 13.sp,
                                        color = TextSecondary.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
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
fun AccountCard(account: Account, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)), // Reduced shadow for consistency
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            NBKBlue.copy(red = NBKBlue.red * 1.15f, green = NBKBlue.green * 1.15f, blue = NBKBlue.blue * 1.1f), // Lighter start
                            NBKBlue,
                            NBKBlue.copy(red = NBKBlue.red * 0.85f, green = NBKBlue.green * 0.85f, blue = NBKBlue.blue * 0.9f), // More contrast
                            NBKBlue.copy(red = NBKBlue.red * 0.7f, green = NBKBlue.green * 0.7f, blue = NBKBlue.blue * 0.8f) // Darker end
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY) // Diagonal gradient
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
                                .background(Color.White.copy(alpha = 0.15f)), // Slightly reduced opacity
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
                                color = Color.White.copy(alpha = 0.9f) // Increased opacity
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
                        tint = Color.White.copy(alpha = 0.7f) // Slightly increased opacity
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Current Balance",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f) // Increased opacity
                    )
                    Text(
                        "KD\u00A0${account.balance}", // Non-breaking space
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
                        color = Color.White.copy(alpha = 0.85f) // Slightly increased opacity
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