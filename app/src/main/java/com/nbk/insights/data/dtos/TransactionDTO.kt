package com.nbk.insights.data.dtos

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionResponse(
    val id: Long? = null,
    val sourceAccountId: Long?,
    val destinationAccountId: Long?,
    val amount: BigDecimal,
    val transactionType: TransactionType?,
    val mcc: MCC,
    val createdAt: String
)

data class MCC(
    val category: String,
    val subCategory: String?
)

enum class Category{
    DINING,
    ENTERTAINMENT,
    SHOPPING,
    FOOD_AND_GROCERIES,
    OTHER
}
enum class TransactionType {
    DEBIT, CREDIT, TRANSFER, WITHDRAW
}