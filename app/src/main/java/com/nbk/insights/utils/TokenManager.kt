package com.nbk.insights.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

class TokenManager(private val prefs: SharedPreferences) {

    fun saveToken(token: String) {
        prefs.edit {
            putString("jwt", token)
            commit()
        }
        Log.d("TokenManager", "Token saved: $token")
    }


    fun getToken(): String? {
        return prefs.getString("jwt", null)
    }

    fun clearToken() {
        prefs.edit { remove("jwt") }
    }

    companion object {
        fun create(context: Context): TokenManager {
            val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            return TokenManager(prefs)
        }
    }
}
