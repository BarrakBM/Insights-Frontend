package com.nbk.insights.network

import com.nbk.insights.data.dtos.AccountsResponse
import com.nbk.insights.data.dtos.BudgetAdherenceResponse
import com.nbk.insights.data.dtos.LimitsRequest
import com.nbk.insights.data.dtos.ListOfLimitsResponse
import com.nbk.insights.data.dtos.SpendingTrendResponse
import com.nbk.insights.data.dtos.TotalBalanceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AccountsApiService {

    @GET("accounts")
    suspend fun retrieveUserAccounts(): Response<AccountsResponse>

    @GET("accounts/total-balance")
    suspend fun retrieveTotalBalance(): Response<TotalBalanceResponse>

    @GET("accounts/limits/{accountId}")
    suspend fun getAccountLimits(@Path("accountId") accountId: Long): Response<ListOfLimitsResponse>

    @POST("accounts/limit")
    suspend fun setAccountLimit(@Body request: LimitsRequest): Response<Map<String, String>>

    // ADD THIS NEW METHOD FOR UPDATING LIMITS
    @PUT("accounts/limit/{limitId}")
    suspend fun updateAccountLimit(
        @Path("limitId") limitId: Long,
        @Body request: LimitsRequest
    ): Response<Map<String, String>>

    @POST("accounts/limits/deactivate/{limitId}")
    suspend fun deactivateLimit(@Path("limitId") limitId: Long): Response<Map<String, String>>

    @GET("accounts/adherence")
    suspend fun getBudgetAdherence(): Response<BudgetAdherenceResponse>

    @GET("accounts/adherence/trends")
    suspend fun getSpendingTrends(): Response<List<SpendingTrendResponse>>
}