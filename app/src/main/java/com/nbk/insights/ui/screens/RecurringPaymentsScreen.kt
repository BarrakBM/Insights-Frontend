package com.nbk.insights.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nbk.insights.ui.composables.BottomNavigationBar

data class RecurringPayment(
    val title: String,
    val amount: String,
    val frequency: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringPaymentsScreen(navController: NavController) {
    val payments = listOf(
        RecurringPayment("Netflix", "KD 3.99", "Monthly"),
        RecurringPayment("Gym Membership", "KD 20.00", "Monthly"),
        RecurringPayment("Cloud Storage", "KD 0.99", "Monthly")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Recurring Payments", color = Color.White, fontSize = 20.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E3A8A))
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedTab = "Recurring", navController = navController)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(payments) { payment ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(payment.title, fontSize = 16.sp, color = Color.Black)
                        Text("Amount: ${payment.amount}", fontSize = 14.sp, color = Color.Gray)
                        Text("Frequency: ${payment.frequency}", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}