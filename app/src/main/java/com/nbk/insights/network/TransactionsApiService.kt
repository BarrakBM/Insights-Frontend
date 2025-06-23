package com.nbk.insights.network
import com.nbk.insights.data.dtos.RecurringPaymentResponse
import com.nbk.insights.data.dtos.TransactionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

}