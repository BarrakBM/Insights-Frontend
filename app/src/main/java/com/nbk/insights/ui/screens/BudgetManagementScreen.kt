package com.nbk.insights.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.*
import com.nbk.insights.ui.composables.*
import com.nbk.insights.ui.theme.*
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.AccountsViewModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetManagementScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val accountsViewModel: AccountsViewModel = viewModel(
        factory = remember { AppInitializer.provideAccountsViewModelFactory(context) }
    )

    val selectedAccount by accountsViewModel.selectedAccount
    val accounts by accountsViewModel.accounts


    // State
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedCategoryAdherence by remember { mutableStateOf<CategoryAdherence?>(null) }

    val budgetAdherence by accountsViewModel.budgetAdherence
    val spendingTrends by accountsViewModel.spendingTrends
    val isLoading by accountsViewModel.isLoading
    val errorMessage by accountsViewModel.errorMessage

    // Fetch data on load
    LaunchedEffect(Unit) {
        accountsViewModel.fetchBudgetAdherence()
        accountsViewModel.fetchSpendingTrends()
        accountsViewModel.fetchUserAccounts()
    }

    // Auto-select first account if none selected
    LaunchedEffect(accounts) {
        if (selectedAccount == null && accounts?.accounts?.isNotEmpty() == true) {
            accounts!!.accounts.first()?.let { accountsViewModel.setSelectedAccount(it) }
        }
    }

    // Handle budget creation
    fun createBudget(category: Category, amount: BigDecimal, renewsAt: String) {
        val accountId = selectedAccount?.accountId ?:  0L

        val request = LimitsRequest(
            category = category.name,
            amount = amount,
            accountId = accountId, // Now guaranteed to be a valid account ID
            renewsAt = LocalDate.parse(renewsAt)
        )
        accountsViewModel.setAccountLimit(request)
        showBudgetDialog = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Budget Management",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showBudgetDialog = true }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Budget",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NBKBlue)
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NBKBlue)
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage ?: "An error occurred",
                                color = Error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    accountsViewModel.fetchBudgetAdherence()
                                    accountsViewModel.fetchSpendingTrends()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NBKBlue)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundLight)
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Overall Budget Summary
                    item {
                        BudgetSummaryCard(budgetAdherence)
                    }

                    // Category Budgets
                    if (budgetAdherence?.categoryAdherences?.isNotEmpty() == true) {
                        item {
                            Text(
                                "Category Budgets",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(budgetAdherence!!.categoryAdherences) { categoryAdherence ->
                            CategoryBudgetCard(
                                categoryAdherence = categoryAdherence,
                                onClick = {
                                    selectedCategoryAdherence = categoryAdherence
                                    showEditDialog = true
                                }
                            )
                        }
                    } else {
                        item {
                            EmptyBudgetState(onCreateBudget = { showBudgetDialog = true })
                        }
                    }

                    // Spending Trends
                    if (!spendingTrends.isNullOrEmpty()) {
                        item {
                            Text(
                                "Spending Trends",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(spendingTrends!!) { trend ->
                            SpendingTrendCard(trend)
                        }
                    }
                }
            }
        }
    }

    // Budget Creation Dialog
    if (showBudgetDialog) {
        BudgetLimitDialog(
            onDismiss = { showBudgetDialog = false },
            onConfirm = { category, amount, renewsAt ->
                createBudget(category, amount, renewsAt)
            }
        )
    }

    // Edit Budget Dialog (simplified version - you'd need to create a proper edit dialog)
    if (showEditDialog && selectedCategoryAdherence != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Budget") },
            text = {
                Text("Editing budget for ${selectedCategoryAdherence!!.category}")
            },
            confirmButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun BudgetSummaryCard(budgetAdherence: BudgetAdherenceResponse?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NBKBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Overall Budget Status",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BudgetMetric(
                    label = "Total Budget",
                    value = "KD ${budgetAdherence?.totalBudget?.toString() ?: "0.000"}",
                    color = Color.White
                )
                BudgetMetric(
                    label = "Total Spent",
                    value = "KD ${budgetAdherence?.totalSpent?.toString() ?: "0.000"}",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Adherence Level Badge
            budgetAdherence?.overallAdherence?.let { level ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = getAdherenceLevelColor(level).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = level.displayName,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = color.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun CategoryBudgetCard(
    categoryAdherence: CategoryAdherence,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                getCategoryColor(categoryAdherence.category).copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getCategoryIcon(categoryAdherence.category),
                            contentDescription = null,
                            tint = getCategoryColor(categoryAdherence.category),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = categoryAdherence.category,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Renews ${categoryAdherence.renewsAt.format(DateTimeFormatter.ofPattern("MMM dd"))}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "KD ${categoryAdherence.spentAmount} / ${categoryAdherence.budgetAmount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (categoryAdherence.adherenceLevel == AdherenceLevel.EXCEEDED) Error else TextPrimary
                    )
                    Text(
                        text = "${categoryAdherence.percentageUsed.toInt()}% used",
                        fontSize = 12.sp,
                        color = getAdherenceLevelColor(categoryAdherence.adherenceLevel)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { (categoryAdherence.percentageUsed / 100f).coerceIn(0.0, 1.0).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = getAdherenceLevelColor(categoryAdherence.adherenceLevel),
                trackColor = Color(0xFFF3F4F6)
            )

            // Spending Trend
            if (categoryAdherence.spendingTrend != SpendingTrend.NO_DATA) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
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
                            SpendingTrend.INCREASED -> Error
                            SpendingTrend.DECREASED -> Success
                            else -> TextSecondary
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${categoryAdherence.spendingChangePercentage.toInt()}% vs last month",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun SpendingTrendCard(trend: SpendingTrendResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trend.category,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = "Budget: KD ${trend.budgetAmount}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (trend.spendingChange > BigDecimal.ZERO)
                            Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (trend.spendingChange > BigDecimal.ZERO) Error else Success,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${trend.spendingChangePercentage.toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (trend.spendingChange > BigDecimal.ZERO) Error else Success
                    )
                }
                Text(
                    text = "vs last month",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun EmptyBudgetState(onCreateBudget: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = NBKBlue.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Budget Set",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start managing your finances by setting up budget limits",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCreateBudget,
                colors = ButtonDefaults.buttonColors(containerColor = NBKBlue)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Your First Budget")
            }
        }
    }
}

// Helper functions - made private to avoid conflicts
private fun getAdherenceLevelColor(level: AdherenceLevel): Color {
    return when (level) {
        AdherenceLevel.EXCELLENT -> Success
        AdherenceLevel.GOOD -> Color(0xFF10B981)
        AdherenceLevel.WARNING -> WarningAmber
        AdherenceLevel.CRITICAL -> Color(0xFFEF4444)
        AdherenceLevel.EXCEEDED -> Error
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category.uppercase()) {
        "DINING" -> Color(0xFFEF4444)
        "SHOPPING" -> Color(0xFF3B82F6)
        "ENTERTAINMENT" -> Color(0xFF8B5CF6)
        "FOOD_AND_GROCERIES" -> Color(0xFFF59E0B)
        "TRANSPORT", "TRANSPORTATION" -> Color(0xFF10B981)
        else -> Color(0xFF6B7280)
    }
}

private fun getCategoryIcon(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category.uppercase()) {
        "DINING" -> Icons.Default.Restaurant
        "SHOPPING" -> Icons.Default.ShoppingBag
        "ENTERTAINMENT" -> Icons.Default.Movie
        "FOOD_AND_GROCERIES" -> Icons.Default.ShoppingCart
        "TRANSPORT", "TRANSPORTATION" -> Icons.Default.DirectionsCar
        else -> Icons.Default.MoreHoriz
    }
}