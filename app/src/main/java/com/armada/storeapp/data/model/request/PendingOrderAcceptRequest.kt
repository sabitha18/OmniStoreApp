package com.armada.storeapp.data.model.request

data class PendingOrderAcceptRequest(
    val id: Int,
    val status: String,
    val storeCode: String,
    val storeID: Int,
    val updateBy: Int
)