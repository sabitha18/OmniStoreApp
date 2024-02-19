package com.armada.storeapp.data.model.response

data class SearchModelResponse(
    val businessDate: String,
    val displayMessage: String,
    val documentNo: String,
    val exceptionMessage: String,
    val grandToatl: Double,
    val iDs: Any,
    val orderID: Any,
    val orderNo: String,
    val recordCount: Int,
    val responseDynamicData: Any,
    val searchEngineDataList: ArrayList<SearchEngineData>,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)