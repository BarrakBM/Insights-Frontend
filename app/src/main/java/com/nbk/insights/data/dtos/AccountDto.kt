package com.nbk.insights.data.dtos

import java.math.BigDecimal


data class AccountsResponse(
    val Accounts: List<Account?>
)

data class Account(
    val accountId: Long,
    val accountType: AccountType,
    val accountNumber: String,
    var balance: BigDecimal,
    val cardNumber: String
    )

enum class AccountType{
    MAIN, SAVINGS
}
data class TotalBalanceResponse(
    val totalBalance: BigDecimal,
)