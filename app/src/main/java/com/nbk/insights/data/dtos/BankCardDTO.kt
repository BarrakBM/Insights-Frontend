package com.nbk.insights.data.dtos

data class BankCardDTO(
    val type: String,
    val name: String,
    val lastFourDigits: String,
    val balance: String,
    val expiryDate: String
)