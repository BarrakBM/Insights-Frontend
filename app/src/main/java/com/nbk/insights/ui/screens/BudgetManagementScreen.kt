package com.nbk.insights.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    val accountLimits by accountsViewModel.accountLimits

    var showBudgetDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedCategoryAdherence by remember { mutableStateOf<CategoryAdherence?>(null) }

    val budgetAdherence by accountsViewModel.budgetAdherence
    val spendingTrends by accountsViewModel.spendingTrends
    val isLoading by accountsViewModel.isLoading
    val errorMessage by accountsViewModel.errorMessage

    LaunchedEffect(Unit) {
        accountsViewModel.fetchBudgetAdherence()
        accountsViewModel.fetchSpendingTrends()
        accountsViewModel.fetchUserAccounts()
    }

    LaunchedEffect(selectedAccount) {
        selectedAccount?.accountId?.let { accountId ->
            accountsViewModel.fetchAccountLimits(accountId)
        }
    }

    LaunchedEffect(accounts) {
        if (selectedAccount == null && accounts?.accounts?.isNotEmpty() == true) {
            accounts!!.accounts.first()?.let { accountsViewModel.setSelectedAccount(it) }
        }
    }

    fun findLimitIdByCategory(category: String): Long? {
        return accountLimits?.accountLimits?.find {
            it?.category?.equals(category, ignoreCase = true) == true
        }?.limitId
    }

    fun budgetExistsForCategory(category: String): Boolean {
        return findLimitIdByCategory(category) != null
    }

    fun createBudget(category: Category, amount: BigDecimal, renewsAt: String) {
        val accountId = selectedAccount?.accountId ?: 0L
        val request = LimitsRequest(
            category = category.name,
            amount = amount,
            accountId = accountId,
            renewsAt = LocalDate.parse(renewsAt)
        )
        accountsViewModel.setAccountLimit(request)
        showBudgetDialog = false
    }

    Scaffold(
        topBar = {
            AppHeaderWithBack(
                title = "Budget Management",
                onBackClick = { navController.popBackStack() },
                action = {
                    IconButton(
                        onClick = { showBudgetDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Budget",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
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
                    item {
                        if (!isLoading && budgetAdherence != null) {
                            BudgetSummaryCard(budgetAdherence)
                        }
                    }

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
                    } else if (!isLoading && budgetAdherence?.categoryAdherences?.isEmpty() == true) {
                        // ✅ FIXED: Only show empty state if not loading and list is confirmed empty
                        item {
                            EmptyBudgetState(onCreateBudget = { showBudgetDialog = true })
                        }
                    }

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

    if (showBudgetDialog) {
        BudgetLimitDialog(
            onDismiss = { showBudgetDialog = false },
            onConfirm = { category, amount, renewsAt ->
                createBudget(category, amount, renewsAt)
            }
        )
    }

    if (showEditDialog && selectedCategoryAdherence != null) {
        BudgetEditDialog(
            categoryAdherence = selectedCategoryAdherence!!,
            onDismiss = { showEditDialog = false },
            onEdit = { category, amount, renewsAt ->
                val limitId = findLimitIdByCategory(selectedCategoryAdherence!!.category)
                val accountId = selectedAccount?.accountId ?: 0L

                val request = LimitsRequest(
                    category = category.name,
                    amount = amount,
                    accountId = accountId,
                    renewsAt = LocalDate.parse(renewsAt)
                )

                if (limitId != null) {
                    accountsViewModel.updateAccountLimit(limitId, request)
                } else {
                    accountsViewModel.setAccountLimit(request)
                }
                showEditDialog = false
            },
            onRemove = {
                val limitId = findLimitIdByCategory(selectedCategoryAdherence!!.category)
                if (limitId != null) {
                    accountsViewModel.deactivateLimit(limitId)
                } else {
                    Log.e("BudgetManagement", "Could not find limit ID for category: ${selectedCategoryAdherence!!.category}")
                }
                showEditDialog = false
            }
        )
    }
}



// Replace the BudgetEditDialog in your BudgetManagementScreen.kt with this corrected version:

@Composable
fun BudgetEditDialog(
    categoryAdherence: CategoryAdherence,
    onDismiss: () -> Unit,
    onEdit: (Category, BigDecimal, String) -> Unit,
    onRemove: () -> Unit // Changed: Remove the limitId parameter since it's handled in the parent
) {
    var showEditForm by remember { mutableStateOf(false) }
    var showRemoveConfirmation by remember { mutableStateOf(false) }

    // Edit form states
    var amount by remember { mutableStateOf(categoryAdherence.budgetAmount.toString()) }
    var renewsAt by remember { mutableStateOf(categoryAdherence.renewsAt.toString()) }

    when {
        showRemoveConfirmation -> {
            AlertDialog(
                onDismissRequest = { showRemoveConfirmation = false },
                title = {
                    Text(
                        text = "Remove Budget",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text("Are you sure you want to remove the budget for ${categoryAdherence.category}?")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Current budget: KD ${categoryAdherence.budgetAmount}",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Text(
                            "Amount spent: KD ${categoryAdherence.spentAmount}",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onRemove() // Call the parent's remove function which handles finding the limitId
                            showRemoveConfirmation = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Error)
                    ) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRemoveConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        showEditForm -> {
            AlertDialog(
                onDismissRequest = { showEditForm = false },
                title = {
                    Text(
                        text = "Edit Budget",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Category (read-only)
                        OutlinedTextField(
                            value = categoryAdherence.category,
                            onValueChange = { },
                            label = { Text("Category") },
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Amount
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Budget Amount (KD)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            prefix = { Text("KD ") }
                        )

                        // Renews At
                        OutlinedTextField(
                            value = renewsAt,
                            onValueChange = { renewsAt = it },
                            label = { Text("Renews At (YYYY-MM-DD)") },
                            placeholder = { Text("2025-12-31") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Current spending info
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = NBKBlue.copy(alpha = 0.1f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "Current spending: KD ${categoryAdherence.spentAmount}",
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${categoryAdherence.percentageUsed.toInt()}% of current budget used",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            try {
                                val category = Category.valueOf(categoryAdherence.category.uppercase())
                                val budgetAmount = BigDecimal(amount)
                                onEdit(category, budgetAmount, renewsAt)
                                showEditForm = false
                            } catch (e: Exception) {
                                // Handle validation errors - you might want to show a toast or error message
                            }
                        }
                    ) {
                        Text("Save Changes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditForm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        else -> {
            // Main action dialog
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
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
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = categoryAdherence.category,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column {
                        // Budget info
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF8FAFC)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Budget:",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "KD ${categoryAdherence.budgetAmount}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Spent:",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "KD ${categoryAdherence.spentAmount}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (categoryAdherence.adherenceLevel == AdherenceLevel.EXCEEDED) Error else TextPrimary
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Renews:",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = categoryAdherence.renewsAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "What would you like to do?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                confirmButton = {
                    Column {
                        // Edit button
                        Button(
                            onClick = { showEditForm = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NBKBlue)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Budget")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Remove button
                        OutlinedButton(
                            onClick = { showRemoveConfirmation = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                            border = BorderStroke(1.dp, Error)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Remove Budget")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }
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
                                getCategoryColor(trend.category).copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getCategoryIcon(trend.category),
                            contentDescription = null,
                            tint = getCategoryColor(trend.category),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = trend.category,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Budget: KD ${trend.budgetAmount}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
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

            Spacer(modifier = Modifier.height(12.dp))

            // Trend Progress Bar
            LinearProgressIndicator(
                progress = {
                    (kotlin.math.abs(trend.spendingChangePercentage.toFloat()) / 100f).coerceIn(0.0f, 1.0f)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (trend.spendingChange > BigDecimal.ZERO) Error else Success,
                trackColor = Color(0xFFF3F4F6)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Additional info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Spending Change",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "KD ${trend.spendingChange}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (trend.spendingChange > BigDecimal.ZERO) Error else Success
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

private fun getCategoryIcon(category: String): ImageVector {
    return when (category.uppercase()) {
        "DINING" -> Icons.Default.Restaurant
        "SHOPPING" -> Icons.Default.ShoppingBag
        "ENTERTAINMENT" -> Icons.Default.Movie
        "FOOD_AND_GROCERIES" -> Icons.Default.ShoppingCart
        "TRANSPORT", "TRANSPORTATION" -> Icons.Default.DirectionsCar
        else -> Icons.Default.MoreHoriz
    }
}