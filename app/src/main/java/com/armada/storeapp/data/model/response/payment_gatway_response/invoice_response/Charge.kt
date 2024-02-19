package com.armada.storeapp.data.model.response.payment_gatway_response.invoice_response

data class Charge(
    val receipt: Receipt,
    val statement_descriptor: String
)