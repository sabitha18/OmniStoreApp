package com.armada.storeapp.data.model.response.payment_gatway_response.invoice_response

data class Order(
    val amount: Double,
    val api_version: String,
    val created: Long,
    val currency: String,
    val id: String,
    val itemAmount: Double,
    val items: List<Item>,
    val live_mode: Boolean,
    val merchant_id: String,
    val `object`: String,
    val shipping: Shipping,
    val status: String,
    val tax: List<Tax>
)