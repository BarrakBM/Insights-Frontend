package com.nbk.insights.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nbk.insights.network.AccountsApiService
import kotlinx.coroutines.launch
import com.nbk.insights.data.dtos.AccountsResponse
import com.nbk.insights.data.dtos.TotalBalanceResponse

class AccountsViewModel(
    private val apiService: AccountsApiService
) : BaseViewModel() {

    private val TAG = "AccountsViewModel"

    private val _accounts = mutableStateOf<AccountsResponse?>(null)
    val accounts: State<AccountsResponse?> get() = _accounts

    private val _totalBalance = mutableStateOf<TotalBalanceResponse?>(null)
    val totalBalance: State<TotalBalanceResponse?> get() = _totalBalance

    fun fetchUserAccounts() {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.retrieveUserAccounts()
                if (response.isSuccessful) {
                    _accounts.value = response.body()
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
}