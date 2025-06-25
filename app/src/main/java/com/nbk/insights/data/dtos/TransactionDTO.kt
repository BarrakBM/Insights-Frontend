package com.nbk.insights.data.dtos

import java.math.BigDecimal
import java.time.LocalDate

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

data class CashFlowCategorizedResponse(
    val moneyIn: BigDecimal,
    val moneyOut: BigDecimal,
    val moneyInByCategory: Map<String, BigDecimal>,
    val moneyOutByCategory: Map<String, BigDecimal>,
    val from: LocalDate,
    val to: LocalDate
) {
    val netCashFlow: BigDecimal
        get() = moneyIn.subtract(moneyOut)
}