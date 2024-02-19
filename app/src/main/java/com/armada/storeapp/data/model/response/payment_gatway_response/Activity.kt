package com.armada.storeapp.data.model.response.payment_gatway_response

data class ActivityX(
    val created: Long,
    val id: String,
    val `object`: String,
    val paid_amount: Int,
    val type: String
)