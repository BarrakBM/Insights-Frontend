package com.nbk.insights.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nbk.insights.data.dtos.CategoryRecommendationResponse
import com.nbk.insights.data.dtos.OfferResponse
import com.nbk.insights.data.dtos.OffersRecommendationResponse
import com.nbk.insights.data.dtos.QuickInsightsDTO
import com.nbk.insights.network.RecommendationsApiService
import kotlinx.coroutines.launch

class RecommendationsViewModel(
    private val apiService: RecommendationsApiService
) : BaseViewModel() {

    private val TAG = "RecommendationsViewModel"

    private val _categoryRecommendations = mutableStateOf<List<CategoryRecommendationResponse>?>(null)
    val categoryRecommendations: State<List<CategoryRecommendationResponse>?> get() = _categoryRecommendations

    private val _offersRecommendation = mutableStateOf<OffersRecommendationResponse?>(null)
    val offersRecommendation: State<OffersRecommendationResponse?> get() = _offersRecommendation

    private val _offersByCategory = mutableStateOf<List<OfferResponse>?>(null)
    val offersByCategory: State<List<OfferResponse>?> get() = _offersByCategory

    private val _quickInsights = mutableStateOf<QuickInsightsDTO?>(null)
    val quickInsights: State<QuickInsightsDTO?> get() = _quickInsights

    fun fetchCategoryRecommendations() {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.getCategoryRecommendations()
                if (response.isSuccessful) {
                    _categoryRecommendations.value = response.body()
                    Log.i(TAG, "Fetched category recommendations successfully.")
                } else {
                    val error = "Failed to fetch recommendations: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching recommendations: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun fetchOffersRecommendation() {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.getOffersRecommendation()
                if (response.isSuccessful) {
                    _offersRecommendation.value = response.body()
                    Log.i(TAG, "Fetched offers recommendation successfully.")
                } else {
                    val error = "Failed to fetch offers recommendation: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching offers recommendation: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun fetchOffersByCategory(category: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.getOffersByCategory(category)
                if (response.isSuccessful) {
                    _offersByCategory.value = response.body()
                    Log.i(TAG, "Fetched offers by category successfully.")
                } else {
                    val error = "Failed to fetch offers by category: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching offers by category: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun fetchQuickInsights() {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.getQuickInsights()
                if (response.isSuccessful) {
                    _quickInsights.value = response.body()
                    Log.i(TAG, "Fetched quick insights successfully.")
                } else {
                    val error = "Failed to fetch quick insights: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching quick insights: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }
}