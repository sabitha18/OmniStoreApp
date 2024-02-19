package com.armada.storeapp.data.model.request

/**
 * Created by PC1 on 09-04-2018.
 */
data class OrderCancelRequestModel(
    val customer_id: String,
    val order_id: String
)