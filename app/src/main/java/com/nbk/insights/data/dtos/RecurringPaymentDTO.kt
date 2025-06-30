package com.nbk.insights.data.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

data class RecurringPaymentResponse(
    val accountId: Long,
    val mcc: MCC,
    val amountGroup: BigDecimal,
    val amounts: List<BigDecimal>,
    val latestAmount: BigDecimal,
    val transactionCount: Int,
    val monthsWithPayments: Int,
    val detectedIntervalRegular: Boolean,
    val confidenceScore: Double,
    val lastDetected: LocalDateTime,
    val skippedPaymentEstimate: List<LocalDateTime>
)

data class AccountInsightsCache(
    val cashFlow: CashFlowCategorizedResponse?,
    val transactions: List<TransactionResponse>?,
    val recurringPayments: List<RecurringPaymentResponse>?
)
