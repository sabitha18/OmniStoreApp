package com.armada.storeapp.data.model.request


/**
 * Created by User999 on 3/8/2018.
 */
data class UpdateCartRequest(
        val cart: Cart? = null
)

data class Cart(
    val customer_id: String? = null,
    val product_id: String? = null,
    val qty: String? = null
)