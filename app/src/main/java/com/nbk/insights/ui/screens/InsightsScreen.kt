package com.nbk.insights.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nbk.insights.data.tempfunctions.getBankCards
import com.nbk.insights.ui.composables.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.asPaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(navController: NavController) {
    val bankCards = remember { getBankCards() }
    var selectedCardId by remember { mutableStateOf<String?>(null) }
    var showInsights by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
                title = {
                    Column {
                        Text(
                            text = "My Cards",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Manage your cards",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E3A8A))
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedTab = "Insights", navController = navController)
        }
    ) { innerPadding ->
        val safePadding = WindowInsets.safeDrawing.asPaddingValues()

        if (showInsights && selectedCardId != null) {
            val selectedCard = bankCards.find { it.lastFourDigits == selectedCardId }
            selectedCard?.let { card ->
                CardInsightContent(
                    card = card,
                    onDismiss = {
                        showInsights = false
                        selectedCardId = null
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(safePadding)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(innerPadding)
                    .padding(safePadding),
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Balance", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                                Text("KD", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                                Text("12,222.21", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Active Cards", fontSize = 12.sp, color = Color.Gray)
                                Text("4", fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("This Month", fontSize = 12.sp, color = Color.Gray)
                                Text("-KD 1,230", fontSize = 16.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                items(bankCards) { card ->
                    CardBarItemWithActions(
                        card = card,
                        onViewInsights = {
                            selectedCardId = card.lastFourDigits
                            showInsights = true
                        },
                        onViewTransactions = {
                            navController.navigate("all_transactions")
                        },
                        onStartBudgeting = { }
                    )
                }
            }
        }
    }
}