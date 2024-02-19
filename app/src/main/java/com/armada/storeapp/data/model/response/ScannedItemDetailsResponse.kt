package com.armada.storeapp.data.model.response

data class ScannedItemDetailsResponse(
    val businessDate: String,
    val displayMessage: String,
    val documentNo: String,
    val esskuimages: Any,
    val exceptionMessage: Any,
    val grandToatl: Int,
    val iDs: Any,
    val orderID: String,
    val orderNo: String,
    val recordCount: Int,
    val responseDynamicData: Any,
    val skuMasterTypesList: ArrayList<SkuMasterTypes>,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)