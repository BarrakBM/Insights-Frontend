package com.nbk.insights.network


import com.nbk.insights.data.dtos.AccountsResponse
import com.nbk.insights.data.dtos.TotalBalanceResponse
import retrofit2.Response
import retrofit2.http.GET

interface AccountsApiService {

    @GET("accounts")
    suspend fun retrieveUserAccounts(): Response<AccountsResponse>

    @GET("accounts/total-balance")
    suspend fun retrieveTotalBalance(): Response<TotalBalanceResponse>

}