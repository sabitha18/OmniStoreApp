package com.armada.storeapp.data.model.response.payment_gatway_response.invoice_response

data class Shipping(
    val amount: Double,
    val currency: String,
    val description: String,
    val id: String,
    val provider: String,
    val service: String
)