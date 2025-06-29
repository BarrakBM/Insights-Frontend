package com.nbk.insights.data.dtos

data class CategoryRecommendationResponse(
    val category: String,
    val recommendation: String
)

data class OffersRecommendationResponse(
    val message: String
)

data class OfferDTO(
    val id: Long,
    val description: String
)

data class QuickInsightsDTO(
    val spendingComparedToLastMonth: String,
    val budgetLimitWarning: String,
    val savingInsights: String
)