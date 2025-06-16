package com.nbk.insights.data.tempfunctions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import com.nbk.insights.data.dtos.BankCardDTO
import com.nbk.insights.data.dtos.TransactionDTO

fun getBankCards(): List<BankCardDTO> {
    return listOf(
        BankCardDTO(
            type = "Debit Card",
            name = "NBK Titanium",
            lastFourDigits = "5678",
            balance = "KD 3,456.78",
            expiryDate = "05/26"
        ),
        BankCardDTO(
            type = "Credit Card",
            name = "NBK World Elite",
            lastFourDigits = "9012",
            balance = "KD 8,765.43",
            expiryDate = "11/25"
        ),
        BankCardDTO(
            type = "Savings",
            name = "Checking",
            lastFourDigits = "8301",
            balance = "$14",
            expiryDate = "N/A"
        ),
        BankCardDTO(
            type = "Credit Card",
            name = "Adv Plus Banking",
            lastFourDigits = "8301",
            balance = "$14",
            expiryDate = "12/27"
        )
    )
}

fun getRecentTransactions(): List<TransactionDTO> {
    return listOf(
        TransactionDTO(
            title = "Starbucks",
            date = "Today, 9:15 AM",
            amount = "-KD 4.75",
            category = "Dining",
            icon = Icons.Default.RestaurantMenu,
            iconColor = Color(0xFFEF4444)
        ),
        TransactionDTO(
            title = "Salary Deposit",
            date = "Yesterday, 10:00 AM",
            amount = "+KD 2,500.00",
            category = "Income",
            icon = Icons.Default.TrendingUp,
            iconColor = Color(0xFF10B981),
            isIncome = true
        ),
        TransactionDTO(
            title = "The Avenues Mall",
            date = "Yesterday, 3:45 PM",
            amount = "-KD 156.25",
            category = "Shopping",
            icon = Icons.Default.ShoppingBag,
            iconColor = Color(0xFF8B5CF6)
        ),
        TransactionDTO(
            title = "Ministry of Electricity",
            date = "May 15, 8:30 AM",
            amount = "-KD 42.50",
            category = "Utilities",
            icon = Icons.Default.Bolt,
            iconColor = Color(0xFFF59E0B)
        ),
        TransactionDTO(
            title = "Netflix",
            date = "May 14",
            amount = "-KD 9.99",
            category = "Entertainment",
            icon = Icons.Default.Movie,
            iconColor = Color(0xFFEF4444)
        )
    )
}

fun getAllTransactions(): List<TransactionDTO> {
    return getRecentTransactions() + listOf(
        TransactionDTO(
            title = "Petrol Station",
            date = "May 13, 7:30 AM",
            amount = "-KD 25.00",
            category = "Transportation",
            icon = Icons.Default.LocalGasStation,
            iconColor = Color(0xFF06B6D4)
        ),
        TransactionDTO(
            title = "Gym Membership",
            date = "May 12, 2:15 PM",
            amount = "-KD 45.00",
            category = "Health & Fitness",
            icon = Icons.Default.FitnessCenter,
            iconColor = Color(0xFF10B981)
        ),
        TransactionDTO(
            title = "Freelance Payment",
            date = "May 11, 11:30 AM",
            amount = "+KD 750.00",
            category = "Income",
            icon = Icons.Default.TrendingUp,
            iconColor = Color(0xFF10B981),
            isIncome = true
        ),
        TransactionDTO(
            title = "Online Shopping",
            date = "May 10, 4:20 PM",
            amount = "-KD 89.99",
            category = "Shopping",
            icon = Icons.Default.ShoppingCart,
            iconColor = Color(0xFF8B5CF6)
        ),
        TransactionDTO(
            title = "Restaurant Dinner",
            date = "May 9, 8:45 PM",
            amount = "-KD 67.50",
            category = "Dining",
            icon = Icons.Default.Restaurant,
            iconColor = Color(0xFFEF4444)
        )
    )
}