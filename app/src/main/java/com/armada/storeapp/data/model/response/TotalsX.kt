package com.armada.storeapp.data.model.response

data class TotalsX(
    val cod_price: String,
    val coupon_code: String,
    val discount_amount: String,
    val gift_credit_used: String,
    val gift_wrap: String,
    val grand_total: String,
    val is_coupon_added: Int,
    val reward_discount: Int,
    val shipping_amount: String,
    val subtotal: String,
    val tax: String,
    val totals: Any,
    val used_points: Int
)