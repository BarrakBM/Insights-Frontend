package com.nbk.insights.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nbk.insights.data.dtos.TransactionResponse
import com.nbk.insights.data.repository.TransactionsRepository
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val transactionsRepository: TransactionsRepository
) : BaseViewModel() {

    private val TAG = "TransactionsViewModel"

    private val _userTransactions = mutableStateOf<List<TransactionResponse>?>(null)
    val userTransactions: State<List<TransactionResponse>?> get() = _userTransactions

    fun fetchUserTransactions(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            setLoading(true)
            try {
                transactionsRepository.getUserTransactions(forceRefresh)
                    .onSuccess { transactionList ->
                        _userTransactions.value = transactionList as List<TransactionResponse>?
                        Log.i(TAG, "Fetched user transactions successfully - ${transactionList.size} transactions.")
                    }
                    .onFailure { exception ->
                        val error = exception.message ?: "Unknown error occurred"
                        Log.e(TAG, "Failed to fetch user transactions: $error", exception)
                        setError(error)
                    }
            } finally {
                setLoading(false)
            }
        }
    }

    fun refreshUserTransactions() {
        fetchUserTransactions(forceRefresh = true)
    }

    fun clearTransactionsCache() {
        transactionsRepository.clearCache()
        _userTransactions.value = null
        Log.i(TAG, "Transactions cache cleared")
    }
}