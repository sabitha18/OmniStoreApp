package com.armada.storeapp.data.model.response

data class DataX(
    val billing_address: BillingAddress,
    val delivery_date: String,
    val expected_delivery: String,
    val failure: String,
    val failure_url: String,
    val hide_from_invoice: Int,
    val items: List<Item>,
    val magento_order_id: String,
    val order_date: String,
    val order_id: String,
    val otp_sent: Boolean,
    val payment_date: String,
    val payment_method: String,
    val payment_url: String,
    val shipping_address: ShippingAddress,
    val status: String,
    val success_url: String,
    val totals: TotalsX
)