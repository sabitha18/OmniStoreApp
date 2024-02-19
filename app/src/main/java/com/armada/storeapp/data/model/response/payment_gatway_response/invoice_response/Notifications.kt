package com.armada.storeapp.data.model.response.payment_gatway_response.invoice_response

data class Notifications(
    val channels: List<String>,
    val dispatch: Boolean
)