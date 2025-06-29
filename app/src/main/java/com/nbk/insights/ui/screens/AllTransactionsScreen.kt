package com.nbk.insights.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.ui.composables.TransactionItem
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.TransactionsViewModel
import com.nbk.insights.ui.theme.*
import java.time.LocalDateTime
import kotlin.math.abs

@SuppressLint("DefaultLocale", "ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
    navController: NavController,
    accountId: Long? = null
) {
    // ✅ Use activity scope instead of composable scope
    val activity = LocalContext.current as ComponentActivity
    val transactionsViewModel: TransactionsViewModel = viewModel(
        viewModelStoreOwner = activity, // 👈 KEY CHANGE
        factory = remember { AppInitializer.provideTransactionsViewModelFactory(activity) }
    )

    // ✅ Only fetch if data doesn't exist or accountId changed
    LaunchedEffect(accountId) {
        if (accountId != null) {
            // Check if we already have this account's transactions
            if (transactionsViewModel.accountTransactions.value == null) {
                transactionsViewModel.fetchAccountTransactions(accountId)
            }
        } else {
            // Check if we already have user transactions
            if (transactionsViewModel.userTransactions.value == null) {
                transactionsViewModel.fetchUserTransactions()
            }
        }
    }

    // Get transactions based on whether accountId is provided
    val allTransactions = if (accountId != null) {
        transactionsViewModel.accountTransactions.value
    } else {
        transactionsViewModel.userTransactions.value
    }

    var searchQuery by remember { mutableStateOf("") }
    val sortOptions = listOf(
        "Most Recent",
        "Oldest → Newest",
        "Amount: High → Low",
        "Amount: Low → High"
    )
    var sortExpanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(sortOptions.first()) }

    /* ---------------- Filter & Sort ---------------- */
    val filtered = allTransactions.orEmpty().filter { tx ->
        val amtStr = String.format("%.3f", abs(tx.amount.toDouble()))
        tx.mcc.category.contains(searchQuery, true) ||
                tx.mcc.subCategory.orEmpty().contains(searchQuery, true) ||
                amtStr.contains(searchQuery) ||
                tx.createdAt.contains(searchQuery, true)
    }

    val sorted = when (selectedSort) {
        sortOptions[1] -> filtered.sortedBy {         // Oldest first
            runCatching { LocalDateTime.parse(it.createdAt) }.getOrNull()
        }
        sortOptions[2] -> filtered.sortedByDescending { it.amount }  // High → Low
        sortOptions[3] -> filtered.sortedBy { it.amount }            // Low → High
        else           -> filtered                                   // Most Recent (API order)
    }

    /* ---------------- UI ---------------- */
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (accountId != null) "Account Transactions" else "All Transactions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NBKBlue)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues),
            contentPadding = PaddingValues(
                vertical = 16.dp,
                horizontal = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            /* ---------- Search bar ---------- */
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text("Search by category, amount or date") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            /* ---------- Sort dropdown ---------- */
            item {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { sortExpanded = true }
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Sort by:", fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(8.dp))
                        Text(selectedSort, color = Color(0xFF1E3A8A))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand", tint = Color(0xFF1E3A8A))
                    }
                    DropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false }
                    ) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedSort = option
                                    sortExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            /* ---------- Transactions ---------- */
            items(sorted) { tx -> TransactionItem(transaction = tx) }
        }
    }
}