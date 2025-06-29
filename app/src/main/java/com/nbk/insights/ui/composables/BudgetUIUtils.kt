package com.nbk.insights.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.nbk.insights.data.dtos.AdherenceLevel
import com.nbk.insights.data.dtos.CategoryAdherence
import com.nbk.insights.ui.theme.*

object BudgetUIUtils {

    fun getCategoryIcon(category: String): ImageVector {
        return when (category.uppercase()) {
            "DINING" -> Icons.Default.Restaurant
            "SHOPPING" -> Icons.Default.ShoppingBag
            "ENTERTAINMENT" -> Icons.Default.Movie
            "FOOD_AND_GROCERIES", "GROCERIES" -> Icons.Default.ShoppingCart
            "TRANSPORT", "TRANSPORTATION" -> Icons.Default.DirectionsCar
            "UTILITIES" -> Icons.Default.Bolt
            "HEALTHCARE" -> Icons.Default.LocalHospital
            else -> Icons.Default.MoreHoriz
        }
    }

    fun getCategoryColor(category: String): Color {
        return when (category.uppercase()) {
            "DINING" -> Color(0xFFEF4444)
            "SHOPPING" -> Color(0xFF3B82F6)
            "ENTERTAINMENT" -> Color(0xFF8B5CF6)
            "FOOD_AND_GROCERIES", "GROCERIES" -> Color(0xFFF59E0B)
            "TRANSPORT", "TRANSPORTATION" -> Color(0xFF10B981)
            "UTILITIES" -> Color(0xFF06B6D4)
            "HEALTHCARE" -> Color(0xFFEC4899)
            else -> Color(0xFF6B7280)
        }
    }

    fun getAdherenceLevelColor(level: AdherenceLevel): Color {
        return when (level) {
            AdherenceLevel.EXCELLENT -> PrimaryBlue
            AdherenceLevel.GOOD -> Success
            AdherenceLevel.WARNING -> WarningAmber
            AdherenceLevel.CRITICAL -> Color(0xFFEF4444)
            AdherenceLevel.EXCEEDED -> Error
        }
    }

    fun getStatusText(categoryAdherence: CategoryAdherence): String {
        val percentageUsed = categoryAdherence.percentageUsed.toInt()
        return when (categoryAdherence.adherenceLevel) {
            AdherenceLevel.EXCELLENT -> "$percentageUsed% used - Doing great"
            AdherenceLevel.GOOD -> "$percentageUsed% used - On track"
            AdherenceLevel.WARNING -> "$percentageUsed% used - Watch your spending"
            AdherenceLevel.CRITICAL -> "$percentageUsed% used - Near limit!"
            AdherenceLevel.EXCEEDED -> "Over budget by ${(percentageUsed - 100)}%"
        }
    }

    fun formatBudgetAmount(amount: java.math.BigDecimal): String {
        return "KD ${amount.setScale(3, java.math.RoundingMode.HALF_UP)}"
    }
}