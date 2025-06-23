package com.nbk.insights.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nbk.insights.data.repository.TransactionsRepository
import com.nbk.insights.network.AccountsApiService
import com.nbk.insights.network.AuthApiService
import com.nbk.insights.network.RetrofitHelper
import com.nbk.insights.network.TransactionApiService
import com.nbk.insights.viewmodels.AccountsViewModel
import com.nbk.insights.viewmodels.AuthViewModel
import com.nbk.insights.viewmodels.TransactionsViewModel
import retrofit2.Retrofit

object AppInitializer {

    fun provideTokenManager(context: Context): TokenManager {
        return TokenManager.create(context)
    }

    private inline fun <reified Api : Any, reified VM : ViewModel> provideViewModelFactory(
        context: Context,
        crossinline apiProvider: (retrofit: Retrofit) -> Api,
        crossinline viewModelProvider: (api: Api, tokenManager: TokenManager) -> VM
    ): ViewModelProvider.Factory {
        val tokenManager = provideTokenManager(context)
        val retrofit = RetrofitHelper.getInstance(tokenManager)
        val api = apiProvider(retrofit)
        return viewModelFactory {
            initializer {
                viewModelProvider(api, tokenManager)
            }
        }
    }

    private inline fun <reified Api : Any, reified VM : ViewModel> provideViewModelFactoryWithoutToken(
        context: Context,
        crossinline apiProvider: (retrofit: Retrofit) -> Api,
        crossinline viewModelProvider: (api: Api) -> VM
    ): ViewModelProvider.Factory {
        val tokenManager = provideTokenManager(context)
        val retrofit = RetrofitHelper.getInstance(tokenManager)
        val api = apiProvider(retrofit)
        return viewModelFactory {
            initializer {
                viewModelProvider(api)
            }
        }
    }

    fun provideAuthViewModelFactory(context: Context): ViewModelProvider.Factory {
        return provideViewModelFactory(
            context,
            { retrofit -> retrofit.create(AuthApiService::class.java) },
            { api, tokenManager -> AuthViewModel(api, tokenManager) }
        )
    }

    fun provideAccountsViewModelFactory(context: Context): ViewModelProvider.Factory {
        return provideViewModelFactoryWithoutToken(
            context,
            { retrofit -> retrofit.create(AccountsApiService::class.java) },
            { api -> AccountsViewModel(api) }
        )
    }

    fun provideTransactionsViewModelFactory(context: Context): ViewModelProvider.Factory {
        return provideViewModelFactoryWithoutToken(
            context,
            { retrofit -> retrofit.create(TransactionApiService::class.java) },
            { api ->
                TransactionsViewModel(
                    transactionsRepository = TransactionsRepository(api),
                    transactionApiService = api
                )
            }
        )
    }
}
