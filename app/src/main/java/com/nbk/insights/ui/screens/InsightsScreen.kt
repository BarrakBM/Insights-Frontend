package com.nbk.insights.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nbk.insights.data.dtos.LimitsRequest
import com.nbk.insights.navigation.Screen
import com.nbk.insights.ui.composables.*
import com.nbk.insights.ui.theme.*
import com.nbk.insights.utils.AppInitializer
import com.nbk.insights.viewmodels.AccountsViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(navController: NavController) {
    // Use activity-scoped ViewModel
    val activity = LocalActivity.current as ComponentActivity
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

    var showBudgetDialog by remember { mutableStateOf(false) }
    var budgetAccountId by remember { mutableStateOf<Long?>(null) }

    // Get accounts from view model
    val accountsResponse = accountsVM.accounts.value
    val accounts = accountsResponse?.accounts ?: emptyList()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Accounts", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Manage your accounts", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
        bottomBar = { BottomNavigationBar(selectedTab = "Insights", navController = navController) }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding)
                .background(BackgroundLight),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Account cards list
            items(accounts) { account ->
                AccountCard(
                    account = account!!,
                    onViewInsights = {
                        accountsVM.setSelectedAccount(account)
                        navController.navigate("card_insights/${account.accountId}")
                    },
                    onViewTransactions = { accountId ->
                        // Now properly navigates to account-specific transactions
                        navController.navigate(Screen.AccountTransactions.createRoute(accountId))
                    },
                    onSetBudget = {
                        accountsVM.setSelectedAccount(account)
                        budgetAccountId = account.accountId  // Set the account ID
                        showBudgetDialog = true
                    }
                )
            }
        }
    }

    // Budget dialog
    if (showBudgetDialog && budgetAccountId != null) {
        BudgetLimitDialog(
            onDismiss = {
                showBudgetDialog = false
                budgetAccountId = null  // Reset account ID when dialog is dismissed
            },
            onConfirm = { category, limit, renewsAt ->
                val limitsRequest = LimitsRequest(
                    category = category.name, // or appropriate string conversion
                    amount = limit,
                    accountId = budgetAccountId!!,
                    renewsAt = if (renewsAt.isNotEmpty()) LocalDate.parse(renewsAt) else null
                )
                accountsVM.setAccountLimit(limitsRequest)
                showBudgetDialog = false
                budgetAccountId = null  // Reset account ID after confirmation
            }
        )
    }
}