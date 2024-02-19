package com.armada.storeapp.data.model.response.payment_gatway_response.invoice_response

data class Tax(
    val description: String,
    val id: String,
    val name: String,
    val rate: Rate
)