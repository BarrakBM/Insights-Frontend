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
import com.nbk.insights.navigation.Screen
import com.nbk.insights.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(navController: NavController) {
    val bankCards = remember { getBankCards() }
    var selectedCardId by remember { mutableStateOf<String?>(null) }
    var showInsights by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
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
                    IconButton(onClick = {
                        navController.navigate(Screen.Notifications.route)
                    }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NBKBlue)
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedTab = "Insights", navController = navController)
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding)
                .background(BackgroundLight),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                SpendingViewAllChart()
            }

            item {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NBKBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = "Insights",
                        tint = Color.White

                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View All Insights", color = Color.White)
                }
            }

            items(bankCards) { card ->
                CardBarItemWithActions(
                    card = card,
                    onViewInsights = {
                        navController.navigate("card_insights/${card.lastFourDigits}")
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