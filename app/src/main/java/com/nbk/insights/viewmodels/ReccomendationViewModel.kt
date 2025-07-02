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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

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

    // New state for storing all category offers
    private val _allCategoryOffers = mutableStateOf<Map<String, List<OfferResponse>>>(emptyMap())
    val allCategoryOffers: State<Map<String, List<OfferResponse>>> get() = _allCategoryOffers

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
                    val offers = response.body() ?: emptyList()

                    // Update the specific category in the map
                    val currentMap = _allCategoryOffers.value.toMutableMap()
                    currentMap[category] = offers
                    _allCategoryOffers.value = currentMap

                    // Also update the single category state for backward compatibility
                    _offersByCategory.value = offers

                    Log.i(TAG, "Fetched offers by category '$category' successfully: ${offers.size} offers")
                } else {
                    val error = "Failed to fetch offers by category '$category': ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching offers by category '$category': ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    // New function to fetch offers for multiple categories simultaneously
    fun fetchOffersForAllCategories(categories: List<String> = listOf("Dining", "Food & Groceries", "Entertainment", "Shopping", "Other")) {
        viewModelScope.launch {
            setLoading(true)
            try {
                // Launch all requests simultaneously
                val deferredResults = categories.map { category ->
                    async {
                        try {
                            val response = apiService.getOffersByCategory(category)
                            if (response.isSuccessful) {
                                category to (response.body() ?: emptyList())
                            } else {
                                Log.w(TAG, "Failed to fetch offers for category '$category': ${response.message()}")
                                category to emptyList<OfferResponse>()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Exception fetching offers for category '$category': ${e.message}", e)
                            category to emptyList<OfferResponse>()
                        }
                    }
                }

                // Wait for all requests to complete
                val results = deferredResults.awaitAll()

                // Update the state with all results
                _allCategoryOffers.value = results.toMap()

                Log.i(TAG, "Fetched offers for all categories successfully")

            } catch (e: Exception) {
                val error = "Exception fetching offers for all categories: ${e.message}"
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

            } finally {
                setLoading(false)
            }
        }
    }

    // Function to fetch all explore screen data at once
    fun fetchExploreScreenData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            // Check if we already have data and don't need to refresh
            if (!forceRefresh &&
                _offersRecommendation.value != null &&
                _allCategoryOffers.value.isNotEmpty()) {
                Log.i(TAG, "Using cached explore screen data")
                return@launch
            }

            setLoading(true)
            try {
                // Updated categories to match your database
                val categories = listOf("Dining", "Food & Groceries", "Entertainment", "Shopping", "Other")

                // Launch offers recommendation and category offers simultaneously
                val offersRecommendationDeferred = async {
                    try {
                        apiService.getOffersRecommendation()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching offers recommendation: ${e.message}", e)
                        null
                    }
                }

                val categoryOffersDeferred = async {
                    categories.map { category ->
                        async {
                            try {
                                val response = apiService.getOffersByCategory(category)
                                if (response.isSuccessful) {
                                    category to (response.body() ?: emptyList())
                                } else {
                                    Log.w(TAG, "Failed to fetch offers for category '$category': ${response.message()}")
                                    category to emptyList<OfferResponse>()
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error fetching offers for $category: ${e.message}", e)
                                category to emptyList<OfferResponse>()
                            }
                        }
                    }.awaitAll()
                }

                // Wait for both operations
                val offersRecommendationResponse = offersRecommendationDeferred.await()
                val categoryOffersResults = categoryOffersDeferred.await()

                // Update states
                offersRecommendationResponse?.let { response ->
                    if (response.isSuccessful) {
                        _offersRecommendation.value = response.body()
                        Log.i(TAG, "Fetched offers recommendation successfully: ${response.body()?.offers?.size ?: 0} offers")
                    } else {
                        Log.w(TAG, "Failed to fetch offers recommendation: ${response.message()}")
                    }
                }

                val categoryOffersMap = categoryOffersResults.toMap()
                _allCategoryOffers.value = categoryOffersMap

                Log.i(TAG, "Fetched all explore screen data successfully. Categories: ${categoryOffersMap.keys}")
                categoryOffersMap.forEach { (category, offers) ->
                    Log.i(TAG, "Category '$category': ${offers.size} offers")
                }

            } catch (e: Exception) {
                val error = "Exception fetching explore screen data: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    // Clear all data
    fun clearData() {
        _categoryRecommendations.value = null
        _offersRecommendation.value = null
        _offersByCategory.value = null
        _allCategoryOffers.value = emptyMap()
        _quickInsights.value = null
        clearError()
    }

    // Add a function to check if we have cached data
    fun hasCachedData(): Boolean {
        return _offersRecommendation.value != null || _allCategoryOffers.value.isNotEmpty()
    }

    // Function to refresh data
    fun refreshExploreData() {
        fetchExploreScreenData(forceRefresh = true)
    }
}