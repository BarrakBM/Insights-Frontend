package com.nbk.insights.data.repository


import android.util.Log
import com.nbk.insights.data.dtos.TransactionResponse
import com.nbk.insights.network.TransactionApiService


class TransactionsRepository(
    private val apiService: TransactionApiService
) {

    private val TAG = "TransactionsRepository"

    // Cache for user transactions
    private var cachedUserTransactions: List<TransactionResponse>? = null

    suspend fun getUserTransactions(forceRefresh: Boolean = false): Result<Any> {
        return try {
            // Return cached data if available and not forcing refresh
            if (!forceRefresh && cachedUserTransactions != null) {
                Log.i(TAG, "Returning cached user transactions")
                return Result.success(cachedUserTransactions!!)
            }

            // Fetch from API
            Log.i(TAG, "Fetching user transactions from API")
            val response = apiService.fetchUserTransactions()

            if (response.isSuccessful) {
                response.body()?.let { transactionList ->
                    // Cache the response
                    cachedUserTransactions = transactionList
                    Log.i(TAG, "User transactions fetched and cached successfully - ${transactionList.size} transactions")
                    Result.success(transactionList)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                val error = "Failed to fetch user transactions: ${response.message()}"
                Log.w(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            val error = "Exception fetching user transactions: ${e.message}"
            Log.e(TAG, error, e)
            Result.failure(e)
        }
    }

    fun clearCache() {
        cachedUserTransactions = null
        Log.i(TAG, "User transactions cache cleared")
    }
}