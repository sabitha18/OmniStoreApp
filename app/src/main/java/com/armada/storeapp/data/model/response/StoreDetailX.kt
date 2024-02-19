package com.armada.storeapp.data.model.response

data class StoreDetailX(
    val countryID: Int,
    val countrySetting: Int,
    val id: Int,
    val location: String,
    val stateID: Int,
    val storeCode: String,
    val storeName: String,
    val wareHouseFullFill: Int,
    val warehouseCode: String
)