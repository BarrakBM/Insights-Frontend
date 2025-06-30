package com.nbk.insights.network

import com.nbk.insights.data.dtos.*
import retrofit2.Response
import retrofit2.http.*

interface TransactionApiService {

    @GET("retrieve/user/transactions")
    suspend fun fetchUserTransactions(): Response<List<TransactionResponse>>

    @GET("retrieve/account/transactions/{accountId}")
    suspend fun fetchAccountTransactions(
        @Path("accountId") accountId: Long,
        @Query("category") category: String? = null,
        @Query("mccId") mccId: Long? = null,
        @Query("period") period: String = "none",
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null
    ): Response<List<TransactionResponse>>

    @GET("retrieve/account/recurring-payments/{accountId}")
    suspend fun detectRecurringPaymentsForAccount(
        @Path("accountId") accountId: Long
    ): Response<List<RecurringPaymentResponse>>

    @GET("retrieve/cash-flow/last/month")
    suspend fun retrieveLastMonth(): Response<CashFlowCategorizedResponse>

    @GET("retrieve/cash-flow/this/month")
    suspend fun retrieveThisMonth(): Response<CashFlowCategorizedResponse>

    @GET("retrieve/cash-flow/this/month/{accountId}")
    suspend fun retrieveAccountThisMonth(
        @Path("accountId") accountId: Long
    ): Response<CashFlowCategorizedResponse>

}