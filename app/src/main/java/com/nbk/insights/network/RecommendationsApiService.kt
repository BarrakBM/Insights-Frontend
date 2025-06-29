package com.nbk.insights.network

import com.nbk.insights.data.dtos.CategoryRecommendationResponse
import com.nbk.insights.data.dtos.OfferDTO
import com.nbk.insights.data.dtos.OffersRecommendationResponse
import com.nbk.insights.data.dtos.QuickInsightsDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RecommendationsApiService {

    @POST("get/recommendations")
    suspend fun getCategoryRecommendations(): Response<List<CategoryRecommendationResponse>>

    @POST("get/offers/recommendation")
    suspend fun getOffersRecommendation(): Response<OffersRecommendationResponse>

    @GET("offer/category")
    suspend fun getOffersByCategory(@Query("category") category: String): Response<List<OfferDTO>>

    @GET("quick-insights")
    suspend fun getQuickInsights(): Response<QuickInsightsDTO>
}