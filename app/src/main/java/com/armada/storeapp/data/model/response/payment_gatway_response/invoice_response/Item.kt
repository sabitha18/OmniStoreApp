package com.armada.storeapp.data.model.response.payment_gatway_response.invoice_response

data class Item(
    val amount: Double,
    val currency: String,
    val description: String,
    val discount: Discount,
    val id: String,
    val image: String,
    val merchant_id: String,
    val name: String,
    val product_id: String,
    val quantity: Int
)