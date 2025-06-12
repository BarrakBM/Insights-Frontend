package com.nbk.insights.network

import com.nbk.insights.data.dtos.*
import com.nbk.insights.data.requests.*
import com.nbk.insights.data.responses.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<TokenResponse>

    @POST("auth/login")
    suspend fun login(@Body user: User): Response<TokenResponse>

    @GET("users/me")
    suspend fun getCurrentUser(): Response<User>

}