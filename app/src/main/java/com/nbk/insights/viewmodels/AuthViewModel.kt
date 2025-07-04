package com.nbk.insights.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nbk.insights.data.dtos.UserDTO
import com.nbk.insights.data.requests.LoginRequest
import com.nbk.insights.data.responses.TokenResponse
import com.nbk.insights.network.AuthApiService
import com.nbk.insights.utils.TokenManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) : BaseViewModel() {

    private val TAG = "AuthViewModel"

    private val _token = mutableStateOf<TokenResponse?>(null)
    val token: State<TokenResponse?> get() = _token

    private val _user = mutableStateOf<UserDTO?>(null)
    val user: State<UserDTO?> get() = _user

    init {
        Log.i(TAG, "Initializing ViewModel and loading stored token")
        loadStoredToken()
    }

    private fun loadStoredToken() {
        val savedToken = tokenManager.getToken()
        Log.i(TAG, "Loaded token from TokenManager: $savedToken")
        _token.value = savedToken?.let { TokenResponse(it) }
        if (savedToken != null) {
            Log.i(TAG, "Valid token found, proceeding to fetch user")
            fetchCurrentUser()
        } else {
            Log.i(TAG, "No token found, skipping user fetch")
        }
    }

    private fun fetchCurrentUser() {
        viewModelScope.launch {
            setLoading(true)
            Log.d(TAG, "Calling API to fetch current user")
            try {
                val response = apiService.getCurrentUser()
                if (response.isSuccessful) {
                    _user.value = response.body()
                    Log.i(TAG, "Fetched user successfully: ${_user.value}")
                } else {
                    val errorMessage = response.message()
                    Log.w(TAG, errorMessage)
                    setError(errorMessage)
                    tokenManager.clearToken()
                    _token.value = null
                }
            } catch (e: Exception) {
                val errorMessage = "Failed to fetch user: ${e.message}"
                Log.e(TAG, errorMessage, e)
                setError(errorMessage)
            } finally {
                setLoading(false)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            setLoading(true)
            Log.d(TAG, "Attempting login for: $email")
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    val msg = errorBody ?: "No error body"
                    Log.w(TAG, msg)
                    setError(msg)
                    return@launch
                }
                val token = response.body()?.token
                if (token.isNullOrBlank()) {
                    val msg = "Login succeeded but token is empty"
                    Log.w(TAG, msg)
                    setError("Empty token")
                    return@launch
                }
                Log.i(TAG, "Login successful, token received: $token")
                tokenManager.saveToken(token)
                _token.value = TokenResponse(token)
                fetchCurrentUser()
            } catch (e: Exception) {
                val msg = "Login exception: ${e.message}"
                Log.e(TAG, msg, e)
                setError(msg)
            } finally {
                setLoading(false)
            }
        }
    }

    fun dummyLogin() {
        Log.i(TAG, "Performing dummy login for testing")
        val dummyToken = "dummy-jwt-token-123456"
        val dummyUser = UserDTO(
            username = "test@nbk.com",
            password = "password123",
            id = 1L,
            fullName = "Test User"
        )
        tokenManager.saveToken(dummyToken)
        _token.value = TokenResponse(dummyToken)
        _user.value = dummyUser
        Log.i(TAG, "Dummy login successful: User = $dummyUser, Token = $dummyToken")
        setLoading(false)
        clearError()
    }

    fun logout() {
        Log.i(TAG, "Logging out user and clearing session data")
        tokenManager.clearToken()
        _token.value = null
        _user.value = null
        clearError()
        setLoading(false)
    }
}