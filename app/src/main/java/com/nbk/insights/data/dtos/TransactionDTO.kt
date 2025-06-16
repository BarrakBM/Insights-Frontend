package com.nbk.insights.data.dtos

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// This is temporary will change it when integrating with the backend

data class TransactionDTO(
    val title: String,
    val date: String,
    val amount: String,
    val category: String,
    val icon: ImageVector,
    val iconColor: Color,
    val isIncome: Boolean = false
)