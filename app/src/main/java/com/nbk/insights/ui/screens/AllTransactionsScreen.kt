package com.nbk.insights.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(navController: NavController) {
    val context = LocalContext.current
    val transactionsViewModel: TransactionsViewModel =
        viewModel(factory = remember { AppInitializer.provideTransactionsViewModelFactory(context) })

    LaunchedEffect(Unit) {
        transactionsViewModel.fetchUserTransactions()
    }
    val allTransactions by transactionsViewModel.userTransactions

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "All Transactions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
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
            items(
                count = allTransactions?.size ?: 0
            ) { index ->
                val transaction = allTransactions?.get(index)
                TransactionItem(transaction = transaction!!)
            }
        }
    }
}