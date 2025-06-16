package com.nbk.insights.data.dtos

data class UserDTO(
    var username: String,
    var password: String,
    val id: Long? = null,
    val token: String? = null
)