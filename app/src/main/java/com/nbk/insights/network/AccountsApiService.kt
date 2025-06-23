package com.nbk.insights.network


import com.nbk.insights.data.dtos.AccountsResponse
import com.nbk.insights.data.dtos.BudgetAdherenceResponse
import com.nbk.insights.data.dtos.LimitsRequest
import com.nbk.insights.data.dtos.TotalBalanceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AccountsApiService {

    @GET("accounts")
    suspend fun retrieveUserAccounts(): Response<AccountsResponse>

    @GET("accounts/total-balance")
    suspend fun retrieveTotalBalance(): Response<TotalBalanceResponse>

    @POST("accounts/limit")
    suspend fun setAccountLimit(@Body request: LimitsRequest): Response<Map<String, String>>

    @POST("accounts/limits/deactivate/{limitId}")
    suspend fun deactivateLimit(@Path("limitId") limitId: Long): Response<Map<String, String>>

    @GET("accounts/adherence")
    suspend fun getBudgetAdherence(): Response<BudgetAdherenceResponse>

    @GET("accounts/adherence/trends")
    suspend fun getSpendingTrends(): Response<List<Map<String, Any>>>

}