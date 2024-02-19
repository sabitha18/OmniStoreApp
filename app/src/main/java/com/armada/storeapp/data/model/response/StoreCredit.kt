package com.armada.storeapp.data.model.response

import java.io.Serializable

data class StoreCredit(
    val balance_remaining: String,
    val balance_used: String,
    val credit_used: String,
    val new_balance: String,
    val total_balance: String
): Serializable