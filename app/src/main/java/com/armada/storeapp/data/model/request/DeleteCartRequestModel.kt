package com.armada.storeapp.data.model.request

data class DeleteCartRequestModel(
    val customer_id: String? = null,
    val cart_item_id: String? = null
)