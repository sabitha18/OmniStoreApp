package com.armada.storeapp.data.model.response

data class Totals(
    val shipping_amount: String,
    val sub_total: Double,
    val subtotal_with_discount: Double,
    val tax: Double,
    val total: Double
)