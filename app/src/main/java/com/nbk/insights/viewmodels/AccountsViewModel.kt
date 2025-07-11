package com.nbk.insights.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nbk.insights.data.dtos.Account
import com.nbk.insights.network.AccountsApiService
import kotlinx.coroutines.launch
import com.nbk.insights.data.dtos.AccountsResponse
import com.nbk.insights.data.dtos.TotalBalanceResponse
import com.nbk.insights.data.dtos.LimitsRequest
import com.nbk.insights.data.dtos.BudgetAdherenceResponse
import com.nbk.insights.data.dtos.ListOfLimitsResponse
import com.nbk.insights.data.dtos.SpendingTrendResponse

class AccountsViewModel(
    private val apiService: AccountsApiService
) : BaseViewModel() {

    private val TAG = "AccountsViewModel"

    private val _accounts = mutableStateOf<AccountsResponse?>(null)
    val accounts: State<AccountsResponse?> get() = _accounts

    private val _selectedAccount = mutableStateOf<Account?>(null)
    val selectedAccount: State<Account?> get() = _selectedAccount

    private val _totalBalance = mutableStateOf<TotalBalanceResponse?>(null)
    val totalBalance: State<TotalBalanceResponse?> get() = _totalBalance

    private val _budgetAdherence = mutableStateOf<BudgetAdherenceResponse?>(null)
    val budgetAdherence: State<BudgetAdherenceResponse?> get() = _budgetAdherence

    private val _spendingTrends = mutableStateOf<List<SpendingTrendResponse>?>(null)
    val spendingTrends: State<List<SpendingTrendResponse>?> get() = _spendingTrends

    private val _accountLimits = mutableStateOf<ListOfLimitsResponse?>(null)
    val accountLimits: State<ListOfLimitsResponse?> get() = _accountLimits

    fun fetchUserAccounts() {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.retrieveUserAccounts()
                if (response.isSuccessful) {
                    _accounts.value = response.body()
                    // set default selected account
                    setSelectedAccount(accounts.value?.accounts?.get(0))
                    Log.i(TAG, "Fetched user accounts successfully.")
                } else {
                    val error = "Failed to fetch accounts: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching accounts: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun setSelectedAccount(account: Account?) {
        if (account != null)
            _selectedAccount.value = account
    }

    fun fetchTotalBalance() {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.retrieveTotalBalance()
                if (response.isSuccessful) {
                    _totalBalance.value = response.body()
                    Log.i(TAG, "Fetched total balance successfully.")
                } else {
                    val error = "Failed to fetch total balance: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching total balance: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun setAccountLimit(limitsRequest: LimitsRequest) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.setAccountLimit(limitsRequest)
                if (response.isSuccessful) {
                    Log.i(TAG, "Account limit set successfully.")
                    // Refresh budget adherence after setting limit
                    fetchBudgetAdherence()
                    // Also refresh account limits
                    selectedAccount.value?.accountId?.let { accountId ->
                        fetchAccountLimits(accountId)
                    }
                } else {
                    val error = "Failed to set account limit: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception setting account limit: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun deactivateLimit(limitId: Long) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.deactivateLimit(limitId)
                if (response.isSuccessful) {
                    Log.i(TAG, "Limit deactivated successfully.")
                    // Refresh data after deactivation
                    fetchBudgetAdherence()
                    selectedAccount.value?.accountId?.let { accountId ->
                        fetchAccountLimits(accountId)
                    }
                } else {
                    val error = "Failed to deactivate limit: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception deactivating limit: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun fetchBudgetAdherence() {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.getBudgetAdherence()
                if (response.isSuccessful) {
                    _budgetAdherence.value = response.body()
                    Log.i(TAG, "Fetched budget adherence successfully.")
                } else {
                    val error = "Failed to fetch budget adherence: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching budget adherence: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    // FIXED: Now uses the proper API method
    fun updateAccountLimit(limitId: Long, limitsRequest: LimitsRequest) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.updateAccountLimit(limitId, limitsRequest)
                if (response.isSuccessful) {
                    Log.i(TAG, "Account limit updated successfully.")
                    // Refresh data after updating
                    fetchBudgetAdherence()
                    selectedAccount.value?.accountId?.let { accountId ->
                        fetchAccountLimits(accountId)
                    }
                } else {
                    val error = "Failed to update account limit: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception updating account limit: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun fetchAccountLimits(accountId: Long) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.getAccountLimits(accountId)
                if (response.isSuccessful) {
                    _accountLimits.value = response.body()
                    Log.i(TAG, "Fetched account limits successfully.")
                } else {
                    val error = "Failed to fetch account limits: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching account limits: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }

    fun fetchSpendingTrends() {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.getSpendingTrends()
                if (response.isSuccessful) {
                    _spendingTrends.value = response.body()
                    Log.i(TAG, "Fetched spending trends successfully.")
                } else {
                    val error = "Failed to fetch spending trends: ${response.message()}"
                    Log.w(TAG, error)
                    setError(error)
                }
            } catch (e: Exception) {
                val error = "Exception fetching spending trends: ${e.message}"
                Log.e(TAG, error, e)
                setError(error)
            } finally {
                setLoading(false)
            }
        }
    }
}