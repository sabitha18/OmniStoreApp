package com.armada.storeapp.data.model.request

/**
 * Created by User999 on 3/9/2018.
 */

data class RivaOrderRequest(
    val customer_id: String,
    val shipping_address_id: String,
    val paymentMethod: String,
    val gateway: String,
    val hide_from_invoice: String,
    val payment_details: OrderRequestPaymentModel? = null
)

data class OrderRequestPaymentModel(
    var id: String? = null,
    val amount: String? = null,
    val date: String? = null,
    val type: String? = null,
    val info: String? = null,
    val status: String? = null
)
