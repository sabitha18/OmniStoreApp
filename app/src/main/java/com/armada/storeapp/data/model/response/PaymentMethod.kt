package com.armada.storeapp.data.model.response

import java.io.Serializable

data class PaymentMethod(
    val cod_price: String,
    val code: String,
    val gateways: String,
    val icon: String,
    val is_selected: Int,
    val title: String
): Serializable