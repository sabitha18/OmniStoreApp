package com.armada.storeapp.data.model.response

import java.io.Serializable

data class OmniStoreStockCheckResponse(
    var status:String,
    var addedQty:Int,
    val acceptedQty: Int,
    val availableQty: Int,
    val brandID: Int,
    val bufferStock: Int,
    val bufferStocks: Int,
    val countryID: Int,
    val enableWarehouseFullFillment: Int,
    val id: Int,
    val points: Int,
    val skuCode: String,
    val stockQty: Int,
    val storeCode: String,
    val storeName: String,
    val storeStock: Int,
    val tapPrepaidmerchandID: String,
    val warehouseCode: String,
    val warehouseID: Int,
    val warehouseName: String,
    val warehousePoints: Int,
    val warehouseStore: Int,
    var idx : Int
):Serializable