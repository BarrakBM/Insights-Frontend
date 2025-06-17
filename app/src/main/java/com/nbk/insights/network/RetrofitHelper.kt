package com.nbk.insights.network

import com.nbk.insights.utils.Constants
import com.nbk.insights.utils.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val baseUrl = "http://10.0.2.2:${Constants.portNumber}/"

    fun getInstance(tokenManager: TokenManager): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor {
                tokenManager.getToken()
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}