package com.nbk.insights.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nbk.insights.network.AuthApiService
import com.nbk.insights.network.RetrofitHelper
import com.nbk.insights.viewmodels.AuthViewModel

object AppInitializer {

    fun provideTokenManager(context: Context): TokenManager {
        return TokenManager.create(context)
    }

    fun provideAuthApiService(tokenManager: TokenManager): AuthApiService {
        val retrofit = RetrofitHelper.getInstance(tokenManager)
        return retrofit.create(AuthApiService::class.java)
    }

    fun provideAuthViewModelFactory(context: Context): ViewModelProvider.Factory {
        val tokenManager = provideTokenManager(context)
        val apiService = provideAuthApiService(tokenManager)

        return viewModelFactory {
            initializer {
                AuthViewModel(apiService, tokenManager)
            }
        }
    }
}
