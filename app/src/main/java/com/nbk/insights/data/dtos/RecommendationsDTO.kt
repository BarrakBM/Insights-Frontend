package com.nbk.insights.data.dtos

data class CategoryRecommendationResponse(
    val category: String,
    val recommendation: String
)

data class OffersRecommendationResponse(
    val message: String,
    val offers: List<OfferBrief>
)

data class OfferBrief(
    val id: Long?,
    val description: String,
    val subCategory: String?,
    val imageUrl: String? = null
)

data class OfferResponse(
    val id: Long? = null,
    val description: String,
    val subCategory: String? = null,
    val imageUrl: String? = null
)
data class QuickInsightsDTO(
    val spendingComparedToLastMonth: String,
    val budgetLimitWarning: String,
    val savingInsights: String
)