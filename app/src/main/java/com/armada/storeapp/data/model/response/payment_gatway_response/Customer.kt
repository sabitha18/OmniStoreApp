package com.armada.storeapp.data.model.response.payment_gatway_response

data class Customer(
    val email: String,
    val first_name: String,
    val id: String,
    val last_name: String,
    val middle_name: String,
    val phone: Phone
)