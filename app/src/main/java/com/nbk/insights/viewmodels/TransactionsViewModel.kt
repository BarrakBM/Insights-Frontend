package com.nbk.insights.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nbk.insights.data.dtos.CashFlowCategorizedResponse
import com.nbk.insights.data.dtos.TransactionResponse
import com.nbk.insights.data.dtos.RecurringPaymentResponse
import com.nbk.insights.data.repository.TransactionsRepository
import com.nbk.insights.network.TransactionApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val transactionApiService: TransactionApiService
) : BaseViewModel() {

    private val TAG = "TransactionsViewModel"

    private val _userTransactions = mutableStateOf<List<TransactionResponse>?>(null)
    val userTransactions: State<List<TransactionResponse>?> get() = _userTransactions

    private val _accountTransactions = mutableStateOf<List<TransactionResponse>?>(null)
    val accountTransactions: State<List<TransactionResponse>?> get() = _accountTransactions

    // Changed from List to single object
    private val _lastMonth = mutableStateOf<CashFlowCategorizedResponse?>(null)
    val lastMonth: State<CashFlowCategorizedResponse?> get() = _lastMonth

    // Changed from List to single object
    private val _thisMonth = mutableStateOf<CashFlowCategorizedResponse?>(null)
    val thisMonth: State<CashFlowCategorizedResponse?> get() = _thisMonth

    private val _thisMonthAccount = mutableStateOf<CashFlowCategorizedResponse?>(null)
    val thisMonthAccount: State<CashFlowCategorizedResponse?> get() = _thisMonthAccount

    private val _recurringPayments = mutableStateOf<List<RecurringPaymentResponse>?>(null)
    val recurringPayments: State<List<RecurringPaymentResponse>?> get() = _recurringPayments

    private val _cashFlowCache = mutableStateOf<Map<Long, CashFlowCategorizedResponse>>(emptyMap())
    val cashFlowCache: State<Map<Long, CashFlowCategorizedResponse>> = _cashFlowCache

    fun fetchUserTransactions(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isRefreshing.value = true        // start spinner
            delay(800)
            setLoading(true)
            try {
                transactionsRepository.getUserTransactions(forceRefresh)
                    .onSuccess { txList ->
                        _userTransactions.value = txList as? List<TransactionResponse>
                        Log.i(TAG, "Fetched $txList user transactions.")
                    }
                    .onFailure { ex ->
                        val err = ex.message ?: "Unknown error"
                        Log.e(TAG, "Fetch failed: $err", ex)
                        setError(err)
                    }
            } finally {
                _isRefreshing.value = false   // stop spinner
                setLoading(false)
            }
        }
    }

    fun fetchAccountTransactions(
        accountId: Long,
        category: String? = null,
        mccId: Long? = null,
        period: String = "none",
        year: Int? = null,
        month: Int? = null
    ) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = transactionApiService.fetchAccountTransactions(
                    accountId = accountId,
                    category = category,
                    mccId = mccId,
                    period = period,
                    year = year,
                    month = month
                )
                if (response.isSuccessful) {
                    _accountTransactions.value = response.body()
                    Log.i(TAG, "Fetched account transactions successfully for account $accountId")
                } else {
                    val error = "Failed to fetch account transactions: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching account transactions: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun detectRecurringPayments(accountId: Long) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = transactionApiService.detectRecurringPaymentsForAccount(accountId)
                if (response.isSuccessful) {
                    _recurringPayments.value = response.body()
                    Log.i(TAG, "Detected recurring payments successfully for account $accountId")
                } else {
                    val error = "Failed to detect recurring payments: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception detecting recurring payments: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun fetchLastMonth(){
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = transactionApiService.retrieveLastMonth()
                if(response.isSuccessful){
                    _lastMonth.value = response.body()
                    Log.i(TAG, "Fetched last month successfully")
                }
                else{
                    val error = "Failed to fetch last month: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            }
            catch (e: Exception) {
                val error = "Exception fetching last month: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun fetchThisMonth(){
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = transactionApiService.retrieveThisMonth()
                if(response.isSuccessful){
                    _thisMonth.value = response.body()
                    Log.i(TAG, "Fetched this month successfully")
                }
                else{
                    val error = "Failed to fetch this month: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            }
            catch (e: Exception) {
                val error = "Exception fetching this month: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }
    fun fetchThisMonthAccount(accountId: Long) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = transactionApiService.retrieveAccountThisMonth(accountId)
                if (response.isSuccessful) {
                    response.body()?.let { cashFlowData ->
                        // Store in cache
                        _cashFlowCache.value += (accountId to cashFlowData)
                        // Set as current
                        _thisMonthAccount.value = cashFlowData
                        Log.i(TAG, "Fetched and cached this month successfully for Account: $accountId")
                    }
                } else {
                    val error = "Failed to fetch this month: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching this month: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun setThisMonthAccountFromCache(accountId: Long) {
        _cashFlowCache.value[accountId]?.let { cachedData ->
            _thisMonthAccount.value = cachedData
            Log.i(TAG, "Using cached data for Account: $accountId")
        }
    }

    fun clearCashFlowCache() {
        _cashFlowCache.value = emptyMap()
    }

    fun clearAccountTransactions() {
        _accountTransactions.value = null
    }

    fun refreshUserTransactions() {
        fetchUserTransactions(forceRefresh = true)
    }

    fun clearTransactionsCache() {
        transactionsRepository.clearCache()
        _userTransactions.value = null
        _accountTransactions.value = null
        _recurringPayments.value = null
        _lastMonth.value = null
        _thisMonth.value = null
        Log.i(TAG, "Transactions cache cleared")
    }
}