package com.armada.storeapp.data.model.request

data class OMNIOrderDetailXX(
    val bC_Price: Double,
    val baseCurrency: String,
    val id: Int,
    val oC_Price: Double,
    val orderCurrency: String,
    val orderID: String,
    val orderQty: Int,
    val packedQty: Int,
    val packedStatus: String,
    val skuCode: String,
    val status: String,
    val weight: Double?,
    val weightUnit: String?
)