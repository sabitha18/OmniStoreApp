package com.armada.storeapp.data.model.response.payment_gatway_response.invoice_details

data class Track(
    val activity: List<Activity>,
    val id: String,
    val `object`: String,
    val status: String,
    val updated: Long
)