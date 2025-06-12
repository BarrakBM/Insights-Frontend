package com.nbk.insights.data.dtos

data class User(
    var email: String,
    var password: String,
    val id: Long? = null,
    val token: String? = null
)