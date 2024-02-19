package com.armada.storeapp.data.model.request

data class OMNIOrderDetailX(
    val baseCurrency: String,
    val bc_Price: Double,
    val brandCode: String,
    val brandID: Int,
    val deliveryType: String,
    val oc_Price: Double,
    val orderCurrency: String,
    val orderQty: Int,
    val pickupDate: String,
    val pickupTimeSlot: String,
    val productGroupID: Int,
    val productGroupName: String,
    val seasonID: Int,
    val seasonName: String,
    val segamentationID: Int,
    val skuCode: String,
    val styleCode: String,
    val subBrandCode: String,
    val subBrandID: Int,
    val weight: String,
    val weightUnit: String,
    val yearID: Int
)