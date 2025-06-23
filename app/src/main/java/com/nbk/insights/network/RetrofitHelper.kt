package com.nbk.insights.network

import com.google.gson.*
import com.nbk.insights.utils.Constants
import com.nbk.insights.utils.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RetrofitHelper {
    private const val baseUrl = "http://10.0.2.2:${Constants.portNumber}/"

    fun getInstance(tokenManager: TokenManager): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor {
                tokenManager.getToken()
            })
            .build()

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return if (src == null) JsonNull.INSTANCE else JsonPrimitive(src.format(formatter))
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate? {
            return if (json == null || json.isJsonNull) null else LocalDate.parse(json.asString, formatter)
        }
    }

    class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return if (src == null) JsonNull.INSTANCE else JsonPrimitive(src.format(formatter))
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime? {
            return if (json == null || json.isJsonNull) null else LocalDateTime.parse(json.asString, formatter)
        }
    }
}