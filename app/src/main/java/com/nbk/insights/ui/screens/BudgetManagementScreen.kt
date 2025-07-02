package com.nbk.insights.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetEditDialog(
    categoryAdherence: CategoryAdherence,
    onDismiss: () -> Unit,
    onEdit: (Category, BigDecimal, String) -> Unit,
    onRemove: () -> Unit
) {
    var showEditForm by remember { mutableStateOf(false) }
    var showRemoveConfirmation by remember { mutableStateOf(false) }

    // Edit form states
    var amount by remember { mutableStateOf(categoryAdherence.budgetAmount.toString()) }
    var selectedDay by remember { mutableStateOf(categoryAdherence.renewsAt.dayOfMonth) }
    var expanded by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val currentDay = today.dayOfMonth

    // Calculate the renewal date based on selected day
    val renewalDate = remember(selectedDay) {
        val targetDate = if (selectedDay <= currentDay) {
            today.withDayOfMonth(selectedDay)
        } else {
            today.minusMonths(1).withDayOfMonth(
                minOf(selectedDay, today.minusMonths(1).lengthOfMonth())
            )
        }
        targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    // Helper function to validate decimal input
    fun isValidDecimalInput(input: String): Boolean {
        if (input.isEmpty()) return true
        val regex = Regex("^\\d*\\.?\\d{0,3}$")
        return regex.matches(input)
    }

    // Calculate percentage for visual indicators
    val percentageUsed = categoryAdherence.percentageUsed
    val remainingBudget = categoryAdherence.budgetAmount - categoryAdherence.spentAmount

    when {
        showRemoveConfirmation -> {
            AlertDialog(
                onDismissRequest = { showRemoveConfirmation = false },
                containerColor = Color.White,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Error.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Error,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Remove Budget",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Are you sure you want to remove the budget for ${formatCategoryName(categoryAdherence.category)}?",
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Error.copy(alpha = 0.05f)
                            ),
                            border = BorderStroke(1.dp, Error.copy(alpha = 0.2f))
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
                                        "Current budget:",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        "KD ${categoryAdherence.budgetAmount}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Amount spent:",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        "KD ${categoryAdherence.spentAmount}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }

                        Text(
                            "This action cannot be undone.",
                            fontSize = 14.sp,
                            color = Error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onRemove()
                            showRemoveConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remove Budget", fontWeight = FontWeight.Medium)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showRemoveConfirmation = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        )
                    ) {
                        Text("Keep Budget", fontWeight = FontWeight.Medium)
                    }
                }
            )
        }

        showEditForm -> {
            Dialog(
                onDismissRequest = { showEditForm = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header with gradient background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            NBKBlue.copy(alpha = 0.08f),
                                            NBKBlue.copy(alpha = 0.02f)
                                        )
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Edit Budget",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "Adjust your spending limits",
                                        fontSize = 14.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { showEditForm = false },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White, CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Category (read-only) with enhanced visual
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Category",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF8FAFC)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    getCategoryColor(categoryAdherence.category).copy(alpha = 0.15f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                getCategoryIcon(categoryAdherence.category),
                                                contentDescription = null,
                                                tint = getCategoryColor(categoryAdherence.category),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                formatCategoryName(categoryAdherence.category),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                "This cannot be changed",
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = TextSecondary.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            // Amount with better validation feedback
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        text = "Monthly Budget Limit",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                    AnimatedVisibility(visible = amount.isNotEmpty()) {
                                        Text(
                                            text = "Max 3 decimal places",
                                            fontSize = 12.sp,
                                            color = if (amountError) Error else TextSecondary.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                OutlinedTextField(
                                    value = amount,
                                    onValueChange = { newValue ->
                                        if (isValidDecimalInput(newValue)) {
                                            amount = newValue
                                            amountError = false
                                        } else {
                                            amountError = true
                                        }
                                    },
                                    label = { Text("Enter amount") },
                                    placeholder = { Text("0.000") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.fillMaxWidth(),
                                    prefix = {
                                        Text(
                                            "KD ",
                                            fontWeight = FontWeight.SemiBold,
                                            color = NBKBlue
                                        )
                                    },
                                    singleLine = true,
                                    isError = amountError || (amount.isNotEmpty() && amount.toBigDecimalOrNull() == null),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NBKBlue,
                                        unfocusedBorderColor = Color(0xFFE5E7EB),
                                        errorBorderColor = Error
                                    ),
                                    supportingText = {
                                        if (amountError) {
                                            Text(
                                                "Invalid format. Use numbers and up to 3 decimal places.",
                                                color = Error,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                )
                            }

                            // Renewal Day Selection with better visual cues
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Budget Renewal Day",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "When your budget resets each month",
                                            fontSize = 12.sp,
                                            color = TextSecondary.copy(alpha = 0.7f),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    // Visual indicator for current day
                                    if (selectedDay == currentDay) {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = NBKBlue.copy(alpha = 0.1f)
                                            ),
                                            border = BorderStroke(1.dp, NBKBlue.copy(alpha = 0.3f))
                                        ) {
                                            Text(
                                                text = "Today",
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                                fontSize = 12.sp,
                                                color = NBKBlue,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    OutlinedTextField(
                                        value = when (selectedDay) {
                                            1 -> "1st of every month"
                                            2 -> "2nd of every month"
                                            3 -> "3rd of every month"
                                            21 -> "21st of every month"
                                            22 -> "22nd of every month"
                                            23 -> "23rd of every month"
                                            31 -> "31st of every month"
                                            else -> "${selectedDay}th of every month"
                                        },
                                        onValueChange = { },
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        label = { Text("Select day") },
                                        leadingIcon = {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .padding(8.dp)
                                                    .background(NBKBlue.copy(alpha = 0.1f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = selectedDay.toString(),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NBKBlue
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NBKBlue,
                                            unfocusedBorderColor = Color(0xFFE5E7EB),
                                            focusedLabelColor = NBKBlue
                                        )
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.heightIn(max = 300.dp)
                                    ) {
                                        (1..31).forEach { day ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            when (day) {
                                                                1 -> "1st"
                                                                2 -> "2nd"
                                                                3 -> "3rd"
                                                                21 -> "21st"
                                                                22 -> "22nd"
                                                                23 -> "23rd"
                                                                31 -> "31st"
                                                                else -> "${day}th"
                                                            },
                                                            fontWeight = if (selectedDay == day) FontWeight.SemiBold else FontWeight.Normal
                                                        )
                                                        if (day == currentDay) {
                                                            Text(
                                                                "Today",
                                                                fontSize = 12.sp,
                                                                color = NBKBlue,
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    selectedDay = day
                                                    expanded = false
                                                },
                                                leadingIcon = {
                                                    if (selectedDay == day) {
                                                        Icon(
                                                            Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = NBKBlue,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                },
                                                colors = MenuDefaults.itemColors(
                                                    textColor = if (selectedDay == day) NBKBlue else Color.Black
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Enhanced current spending info with progress bar
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        percentageUsed > 90 -> Error.copy(alpha = 0.08f)
                                        percentageUsed > 75 -> Color(0xFFFEF3C7)
                                        else -> NBKBlue.copy(alpha = 0.08f)
                                    }
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    when {
                                        percentageUsed > 90 -> Error.copy(alpha = 0.2f)
                                        percentageUsed > 75 -> Color(0xFFF59E0B).copy(alpha = 0.3f)
                                        else -> NBKBlue.copy(alpha = 0.2f)
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = null,
                                            tint = when {
                                                percentageUsed > 90 -> Error
                                                percentageUsed > 75 -> Color(0xFFF59E0B)
                                                else -> NBKBlue
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "Current Spending Status",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    }

                                    // Progress bar
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFE5E7EB))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(fraction = (percentageUsed.toFloat() / 100).coerceIn(0f, 1f))
                                                    .fillMaxHeight()
                                                    .background(
                                                        getAdherenceLevelColor(categoryAdherence.adherenceLevel),
                                                        RoundedCornerShape(4.dp)
                                                    )
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "KD ${categoryAdherence.spentAmount}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${percentageUsed.toInt()}%",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = getAdherenceLevelColor(categoryAdherence.adherenceLevel)
                                            )
                                        }
                                    }

                                    Divider(color = Color(0xFFE5E7EB))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Remaining:",
                                            fontSize = 14.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = if (remainingBudget >= BigDecimal.ZERO)
                                                "KD $remainingBudget"
                                            else
                                                "KD ${remainingBudget.abs()} over",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (remainingBudget >= BigDecimal.ZERO)
                                                TextPrimary
                                            else
                                                Error
                                        )
                                    }
                                }
                            }

                            // Action buttons with better spacing
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showEditForm = false },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = TextPrimary
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                                ) {
                                    Text("Cancel", fontWeight = FontWeight.Medium)
                                }

                                Button(
                                    onClick = {
                                        try {
                                            val category = Category.valueOf(categoryAdherence.category.uppercase())
                                            val budgetAmount = BigDecimal(amount)
                                            if (budgetAmount > BigDecimal.ZERO) {
                                                onEdit(category, budgetAmount, renewalDate)
                                                showEditForm = false
                                            }
                                        } catch (e: Exception) {
                                            amountError = true
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NBKBlue),
                                    enabled = amount.isNotEmpty() && !amountError && amount.toBigDecimalOrNull() != null
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Save Changes", fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }

        else -> {
            // Enhanced main action dialog
            AlertDialog(
                onDismissRequest = onDismiss,
                containerColor = Color.White,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    getCategoryColor(categoryAdherence.category).copy(alpha = 0.15f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                getCategoryIcon(categoryAdherence.category),
                                contentDescription = null,
                                tint = getCategoryColor(categoryAdherence.category),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = formatCategoryName(categoryAdherence.category),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Budget Management",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Budget overview card with progress indicator
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF8FAFC)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Visual progress indicator
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFFE5E7EB))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = (percentageUsed.toFloat() / 100f).coerceIn(0f, 1f))
                                            .fillMaxHeight()
                                            .background(
                                                getAdherenceLevelColor(categoryAdherence.adherenceLevel),
                                                RoundedCornerShape(3.dp)
                                            )
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Budget", fontSize = 12.sp, color = TextSecondary)
                                        Text(
                                            "KD ${categoryAdherence.budgetAmount}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Spent", fontSize = 12.sp, color = TextSecondary)
                                        Text(
                                            "KD ${categoryAdherence.spentAmount}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (categoryAdherence.adherenceLevel == AdherenceLevel.EXCEEDED)
                                                Error else TextPrimary
                                        )
                                    }
                                }

                                Divider(color = Color(0xFFE5E7EB))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = NBKBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            "Renews on day ${categoryAdherence.renewsAt.dayOfMonth}",
                                            fontSize = 14.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    // Status badge
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = when (categoryAdherence.adherenceLevel) {
                                                AdherenceLevel.GOOD -> Color(0xFF10B981).copy(alpha = 0.1f)
                                                AdherenceLevel.WARNING -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                                                AdherenceLevel.EXCEEDED -> Error.copy(alpha = 0.1f)
                                                else -> Color(0xFF10B981).copy(alpha = 0.1f) // Default case
                                            }
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            when (categoryAdherence.adherenceLevel) {
                                                AdherenceLevel.GOOD -> Color(0xFF10B981).copy(alpha = 0.3f)
                                                AdherenceLevel.WARNING -> Color(0xFFF59E0B).copy(alpha = 0.3f)
                                                AdherenceLevel.EXCEEDED -> Error.copy(alpha = 0.3f)
                                                else -> Color(0xFF10B981).copy(alpha = 0.3f) // Default case
                                            }
                                        )
                                    ) {
                                        Text(
                                            text = when (categoryAdherence.adherenceLevel) {
                                                AdherenceLevel.GOOD -> "On Track"
                                                AdherenceLevel.WARNING -> "Warning"
                                                AdherenceLevel.EXCEEDED -> "Over Budget"
                                                else -> "On Track" // Default case
                                            },
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = when (categoryAdherence.adherenceLevel) {
                                                AdherenceLevel.GOOD -> Color(0xFF10B981)
                                                AdherenceLevel.WARNING -> Color(0xFFF59E0B)
                                                AdherenceLevel.EXCEEDED -> Error
                                                else -> Color(0xFF10B981) // Default case
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "What would you like to do?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showEditForm = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NBKBlue),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Budget", fontWeight = FontWeight.Medium)
                        }

                        OutlinedButton(
                            onClick = { showRemoveConfirmation = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                            border = BorderStroke(1.dp, Error.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Remove Budget", fontWeight = FontWeight.Medium)
                        }

                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Cancel",
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                dismissButton = null
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

            // Adherence Level Badge with percentage
            budgetAdherence?.let { data ->
                val percentageUsed = if (data.totalBudget > BigDecimal.ZERO) {
                    ((data.totalSpent / data.totalBudget) * BigDecimal(100)).toInt()
                } else 0

                data.overallAdherence?.let { level ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = getAdherenceLevelColor(level).copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = level.displayName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "• ${percentageUsed}% used",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                    }
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
                            text = formatCategoryName(categoryAdherence.category),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Renews on day ${categoryAdherence.renewsAt.dayOfMonth}",
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
                            text = formatCategoryName(trend.category),
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